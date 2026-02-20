package net.psunset.translatorpp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.psunset.translatorpp.api.ChatComponentMixinAccessor;
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
@Mixin(ChatComponent.class)
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

    @WrapOperation(method = "addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ComponentRenderUtils;wrapComponents(Lnet/minecraft/network/chat/FormattedText;ILnet/minecraft/client/gui/Font;)Ljava/util/List;"))
    private List<FormattedCharSequence> wrapWrapComponents(FormattedText formattedText, int i, Font font, Operation<List<FormattedCharSequence>> original) {
        List<FormattedCharSequence> toReturn = original.call(formattedText, i, font);
        int s = toReturn.size();
        for (int x = 99; x >= s; x--) {
            this.translatorpp$messageIndexTrimmedToAll[x] = this.translatorpp$messageIndexTrimmedToAll[x - s] + 1;
        }
        Arrays.fill(this.translatorpp$messageIndexTrimmedToAll, 0, s, 0);
        return toReturn;
    }

    @Unique
    @Override
    @Nullable
    public String translatorpp$getMessageContentAt(double x, double y) {
        double cX = this.screenToChatX(x);
        double cY = this.screenToChatY(y);
        int i = this.getMessageLineIndexAt(cX, cY);
        if (i >= 0 && i < this.translatorpp$messageIndexTrimmedToAll.length) {
            int idx = this.translatorpp$messageIndexTrimmedToAll[i];
            if (idx >= 0 && idx < this.allMessages.size()) {
                return this.allMessages.get(idx).content().getString();
            }
        }
        return null;
    }

    @Unique
    @Override
    public int[] translatorpp$getMessageIndexTrimmedToAll() {
        return translatorpp$messageIndexTrimmedToAll;
    }
}
