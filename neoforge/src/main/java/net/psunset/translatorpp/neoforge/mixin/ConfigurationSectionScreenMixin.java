package net.psunset.translatorpp.neoforge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * OpenAI API Key maybe too long to got cut in default EditBox.
 * So we increase the max length of the EditBox when StringValue is created.
 */
@Mixin(ConfigurationScreen.ConfigurationSectionScreen.class)
public abstract class ConfigurationSectionScreenMixin extends OptionsSubScreen {
    public ConfigurationSectionScreenMixin(Screen arg, Options arg2, Component arg3) {
        super(arg, arg2, arg3);
    }

    @Inject(method = "createStringValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setMaxLength(I)V", shift = At.Shift.AFTER))
    public void onCreateStringValue(final String key, final Predicate<String> tester, final Supplier<String> source, final Consumer<String> target, CallbackInfoReturnable<ConfigurationScreen.ConfigurationSectionScreen.Element> cir, @Local EditBox editbox) {
        if (key.equals("openai_apikey")) {
            editbox.setMaxLength(192); // Directly increase the max length of the edit box here.
        }
    }
}
