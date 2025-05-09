package net.psunset.translatorpp.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.terraformersmc.modmenu.gui.ModsScreen;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ModsScreen.class)
public abstract class ModsScreenMixin extends Screen {

    @Shadow private AbstractWidget configureButton;

    protected ModsScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "updateSelectedEntry", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.AFTER))
    public void onUpdateSelectedEntry(ModListEntry entry, CallbackInfo ci, @Local LocalRef<String> modId) {
        this.configureButton.setTooltip(Tooltip.create(Component.translatable("misc.translatorpp.missing.clothconfig")));
    }
}
