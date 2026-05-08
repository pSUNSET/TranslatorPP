package net.psunset.translatorpp.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.psunset.translatorpp.core.TranslationKit;
import net.psunset.translatorpp.tool.TooltipUtl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BookViewScreen.class)
public abstract class BookViewScreenMixin extends Screen {

    @Shadow
    @Final
    protected static int IMAGE_WIDTH;

    @Shadow
    @Final
    protected static int IMAGE_HEIGHT;

    @Shadow
    private BookViewScreen.BookAccess bookAccess;

    @Shadow
    protected abstract int backgroundLeft();

    @Shadow
    protected abstract int backgroundTop();

    protected BookViewScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "render", at = @At("TAIL"), cancellable = true)
    private void translatorpp$onRender(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if (i >= this.backgroundLeft() && i <= this.backgroundLeft() + IMAGE_WIDTH &&
                j >= this.backgroundTop() && j <= this.backgroundTop() + IMAGE_HEIGHT) {
            var pages = this.bookAccess.pages();
            TranslationKit.getInstance().setHoveredText(pages);

            if (TranslationKit.getInstance().isTranslated() &&
                    TranslationKit.getInstance().getTranslatedResult() != null &&
                    TooltipUtl.getCombinedTooltipText(pages).equals(TranslationKit.getInstance().getTranslatedText())) {
                var style = Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(TranslationKit.getInstance().createResultForChat()));
                guiGraphics.renderComponentHoverEffect(this.font, style, i, j);
                ci.cancel();
            }
        }
    }
}
