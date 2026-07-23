package net.psunset.translatorpp.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.api.ChatComponentAccessor;
import net.psunset.translatorpp.core.TranslationKit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    protected ChatScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"), cancellable = true)
    private void translatorpp$onExtractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        String text = ((ChatComponentAccessor) this.minecraft.gui.getChat()).translatorpp$getMessageContentAt(mouseX, mouseY);
        TranslationKit.getInstance().setHoveredText(text);

        if (text != null &&
                TranslationKit.getInstance().isTranslated() &&
                TranslationKit.getInstance().getTranslatedResult() != null &&
                text.equals(TranslationKit.getInstance().getTranslatedText())) {
            graphics.setTooltipForNextFrame(this.font, TranslationKit.getInstance().createResultForChat(), mouseX, mouseY);
            ci.cancel();
        }
    }
}
