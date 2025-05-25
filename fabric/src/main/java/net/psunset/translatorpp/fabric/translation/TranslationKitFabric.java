package net.psunset.translatorpp.fabric.translation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.TranslationKit;

public class TranslationKitFabric {

    @Environment(EnvType.CLIENT)
    public static void init() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof AbstractContainerScreen<?>) {
                ScreenKeyboardEvents.afterKeyPress(screen).register((_screen, key, scancode, modifiers) -> {
                    if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                        TranslationKit.getInstance().start(client);
                    }
                });

                ScreenKeyboardEvents.afterKeyRelease(screen).register(((_screen, key, scancode, modifiers) -> {
                    if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                        TranslationKit.getInstance().stop();
                    }
                }));

                ScreenEvents.remove(screen).register(_screen -> {
                    TranslationKit.getInstance().stop();
                });
            }
        });
    }
}
