package net.psunset.translatorpp.fabric.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.psunset.translatorpp.fabric.translation.TranslationKit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * To let the {@link TranslationKit#hoveredStack} got updated on time.
 */
@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {

    @Shadow
    protected Slot hoveredSlot;

    private AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void afterRender(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        TranslationKit.getInstance().hoveredStack = this.hoveredSlot != null && this.hoveredSlot.hasItem() ? this.hoveredSlot.getItem() : null;
    }
}
