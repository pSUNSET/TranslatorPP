package net.psunset.translatorpp.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;getComponentStyleAt(DD)Lnet/minecraft/network/chat/Style;", shift = At.Shift.BEFORE), cancellable = true)
    private void translatorpp$onRender(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        String text = ((ChatComponentMixinAccessor) this.minecraft.gui.getChat()).translatorpp$getMessageContentAt(i, j);
        TranslationKit.getInstance().setHoveredText(text);

        if (text != null &&
                TranslationKit.getInstance().isTranslated() &&
                TranslationKit.getInstance().getTranslatedResult() != null &&
                text.equals(TranslationKit.getInstance().getTranslatedText())) {
            var style = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TranslationKit.getInstance().createResultForChat()));
            this.renderComponentHoverEffect(poseStack, style, i, j);
            ci.cancel();
        }
    }
}
