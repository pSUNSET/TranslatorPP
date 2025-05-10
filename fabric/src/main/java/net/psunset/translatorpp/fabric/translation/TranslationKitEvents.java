package net.psunset.translatorpp.fabric.translation;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.TranslationKit;

public class TranslationKitEvents {

    public static void commonInit() {
        ScreenEvents.AFTER_INIT.register((client, _screen, scaledWidth, scaledHeight) -> {
            if (_screen instanceof AbstractContainerScreen<?>) {
                ScreenKeyboardEvents.afterKeyPress(_screen).register((screen, key, scancode, modifiers) -> {
                    if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                        TranslationKit.getInstance().start(client.player);
                    }

                });

                ScreenKeyboardEvents.afterKeyRelease(_screen).register(((screen, key, scancode, modifiers) -> {
                    if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                        TranslationKit.getInstance().stop();

                    }
                }));

                ScreenEvents.remove(_screen).register(screen -> {
                    TranslationKit.getInstance().stop();

                });
            }
        });

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (TranslationKit.getInstance().isTranslated() && stack.equals(TranslationKit.getInstance().getTranslatedStack()) &&
                    TranslationKit.getInstance().getTranslatedResult() != null) {
                lines.add(1, TranslationKit.getInstance().getTranslatedResult());
            }
        });
    }
}
