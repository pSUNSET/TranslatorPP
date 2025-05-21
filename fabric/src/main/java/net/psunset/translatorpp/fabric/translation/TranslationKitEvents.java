package net.psunset.translatorpp.fabric.translation;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.TranslationKit;

public class TranslationKitEvents {

    public static void commonInit() {
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

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
            if (TranslationKit.getInstance().isTranslated() && stack.equals(TranslationKit.getInstance().getTranslatedStack()) &&
                    TranslationKit.getInstance().getTranslatedResult() != null) {
                lines.add(1, TranslationKit.getInstance().getTranslatedResult());
            }
        });
    }
}
