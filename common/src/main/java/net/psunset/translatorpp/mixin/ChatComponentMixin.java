package net.psunset.translatorpp.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.psunset.translatorpp.mixinaccess.ChatComponentMixinAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(value = ChatComponent.class, remap = false)
public abstract class ChatComponentMixin implements ChatComponentMixinAccessor {
    @Unique
    private final int[] translatorpp$messageIndexTrimmedToAll = Util.make(new int[100], arr -> Arrays.fill(arr, -1));

    @Shadow
    protected abstract int getMessageLineIndexAt(double d, double e);

    @Shadow
    protected abstract double screenToChatX(double d);

    @Shadow
    protected abstract double screenToChatY(double d);

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract double getScale();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "clearMessages(Z)V", at = @At("TAIL"))
    private void afterClearMessages(boolean bl, CallbackInfo ci) {
        Arrays.fill(this.translatorpp$messageIndexTrimmedToAll, -1);
    }

    @Inject(method = "refreshTrimmedMessages()V", at = @At("HEAD"))
    private void beforeRefreshTrimmedMessages(CallbackInfo ci) {
        Arrays.fill(this.translatorpp$messageIndexTrimmedToAll, -1);
    }

    @Inject(method = "addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V", at = @At("TAIL"))
    private void afterAddMessageToDisplayQueue(GuiMessage guiMessage, CallbackInfo ci /*, @Local List list*/) {
        // ---
        int i = Mth.floor(this.getWidth() / this.getScale());
        GuiMessageTag.Icon icon = guiMessage.icon();
        if (icon != null) {
            i -= icon.width + 4 + 2;
        }

        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(guiMessage.content(), i, this.minecraft.font);
        // --- `list` should be available here via @Local, but it failed for some reason I can't figure out.
        int s = list.size();
        for (int x = 99; x - s >= 0; x--) {
                this.translatorpp$messageIndexTrimmedToAll[x] = this.translatorpp$messageIndexTrimmedToAll[x - s] + 1;
        }
        Arrays.fill(this.translatorpp$messageIndexTrimmedToAll, 0, s, 0);
    }

    @Unique
    @Nullable
    public String translatorpp$getMessageContentAt(double x, double y) {
        double cX = this.screenToChatX(x);
        double cY = this.screenToChatY(y);
        int i = this.getMessageLineIndexAt(cX, cY);
        if (i >= 0 && i < 100) {
            int idx = this.translatorpp$messageIndexTrimmedToAll[i];
            return this.allMessages.get(idx).content().getString();
        } else {
            return null;
        }
    }
}
