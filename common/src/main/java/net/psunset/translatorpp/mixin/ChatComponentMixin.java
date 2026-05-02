package net.psunset.translatorpp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
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
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    protected abstract int getWidth();

    @Shadow
    protected abstract double getScale();

    @Shadow
    @Final
    Minecraft minecraft;

    @Shadow
    public abstract boolean isChatFocused();

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    public abstract int getLinesPerPage();

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    protected abstract int getLineHeight();

    @Inject(method = "clearMessages(Z)V", at = @At("TAIL"))
    private void translatorpp$afterClearMessages(boolean bl, CallbackInfo ci) {
        Arrays.fill(this.translatorpp$messageIndexTrimmedToAll, -1);
    }

    @Inject(method = "refreshTrimmedMessages()V", at = @At("HEAD"))
    private void translatorpp$beforeRefreshTrimmedMessages(CallbackInfo ci) {
        Arrays.fill(this.translatorpp$messageIndexTrimmedToAll, -1);
    }

    @WrapOperation(method = "addMessageToDisplayQueue(Lnet/minecraft/client/GuiMessage;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/GuiMessage;splitLines(Lnet/minecraft/client/gui/Font;I)Ljava/util/List;"))
    private List<FormattedCharSequence> translatorpp$wrapSplitLines(GuiMessage instance, Font font, int i, Operation<List<FormattedCharSequence>> original) {
        List<FormattedCharSequence> toReturn = original.call(instance, font, i);
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
    public String translatorpp$getMessageContentAt(double globalMouseX, double globalMouseY) {
        double mouseX = this.translatorpp$screenToChatX(globalMouseX);
        double mouseY = this.translatorpp$screenToChatY(globalMouseY);
        int i = this.translatorpp$getMessageLineIndexAt(mouseX, mouseY);
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

    /**
     * [Vanilla Copy] Original one got removed beyond 1.21.11
     */
    @Unique
    @Override
    public double translatorpp$screenToChatX(double x) {
        return x / this.getScale() - (double) 4.0F;
    }

    /**
     * [Vanilla Copy] Original one got removed beyond 1.21.11
     */
    @Unique
    @Override
    public double translatorpp$screenToChatY(double y) {
        double d = (double) this.minecraft.getWindow().getGuiScaledHeight() - y - (double) 40.0F;
        return d / (this.getScale() * (double) this.getLineHeight());
    }

    /**
     * [Vanilla Copy] Original one got removed beyond 1.21.11
     */
    @Unique
    @Override
    public int translatorpp$getMessageLineIndexAt(double mouseX, double mouseY) {
        if (this.isChatFocused() && !this.isChatHidden()) {
            if (!(mouseX < (double) -4.0F) && !(mouseX > (double) Mth.floor((double) this.getWidth() / this.getScale()))) {
                int i = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
                if (mouseY >= (double) 0.0F && mouseY < (double) i) {
                    int j = Mth.floor(mouseY + (double) this.chatScrollbarPos);
                    if (j >= 0 && j < this.trimmedMessages.size()) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }
}
