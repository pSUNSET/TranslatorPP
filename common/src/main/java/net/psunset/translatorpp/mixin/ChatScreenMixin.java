package net.psunset.translatorpp.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.psunset.translatorpp.api.ChatComponentMixinAccessor;
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

    @Inject(method = "render", at = @At("TAIL"), cancellable = true)
    private void translator$onRender(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        String text = ((ChatComponentMixinAccessor) this.minecraft.gui.getChat()).translatorpp$getMessageContentAt(i, j);
        TranslationKit.getInstance().setHoveredText(text);

        if (text != null &&
                TranslationKit.getInstance().isTranslated() &&
                TranslationKit.getInstance().getTranslatedResult() != null &&
                text.equals(TranslationKit.getInstance().getTranslatedText())) {
            var style = Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(TranslationKit.getInstance().createResultForChat()));
            guiGraphics.renderComponentHoverEffect(this.font, style, i, j);
            ci.cancel();
        }
    }
}
