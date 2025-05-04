package net.psunset.translatorpp.fabric.translation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.config.TPPConfig;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.GoogleTranslationClientTool;

import java.util.concurrent.atomic.AtomicInteger;

public class TranslationKit {

    private static TranslationKit INSTANCE;

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    public static TranslationKit getInstance() {
        return INSTANCE;
    }

    public ItemStack hoveredStack = null;
    private ItemStack translatedStack = null;
    private String translatedResult = null;
    private boolean translated = false;
    private Thread translationThread = null;

    public TranslationKit() {
    }

    public void translate() {
        translated = true;
        if (hoveredStack == null || hoveredStack.equals(translatedStack)) return;
        translationThread = null;

        translatedStack = hoveredStack;
        translatedResult = I18n.get("misc.translatorpp.translation.waiting");
        translationThread = createTranslationThread();
        translationThread.start();
    }

    private Thread createTranslationThread() {
        return new Thread(() -> {
            translatedResult = GoogleTranslationClientTool.getInstance().translate(
                    translatedStack.getHoverName().getString(), TPPConfig.getInstance().sourceLanguage, TPPConfig.getInstance().targetLanguage);
        }, "Translation thread-" + taskCounter.incrementAndGet());
    }

    public void stop() {
        translationThread = null;
        translated = false;
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        TranslatorPP.LOGGER.debug("Initializing TranslationKit");
        INSTANCE = new TranslationKit();
    }

    public static void commonInit() {
        ScreenEvents.AFTER_INIT.register((client, _screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.afterKeyPress(_screen).register((screen, key, scancode, modifiers) -> {
                if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                    INSTANCE.translate();
                }
            });
            ScreenKeyboardEvents.afterKeyRelease(_screen).register((screen, key, scancode, modifiers) -> {
                if (TPPKeyMappings.TRANSLATE_KEY.matches(key, scancode)) {
                    INSTANCE.stop();
                }
            });
        });

        ItemTooltipCallback.EVENT.register(((stack, tooltipContext, tooltipType, lines) -> {
            if (INSTANCE.translated) {
                lines.add(1, Component.translatable("misc.translatorpp.translation", INSTANCE.translatedResult));
            }
        }));
    }
}
