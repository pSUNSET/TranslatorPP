package net.psunset.translatorpp.neoforge.translation;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.neoforge.config.TPPConfig;
import net.psunset.translatorpp.translation.GoogleTranslationAPITool;

import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = TranslatorPP.ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class TranslationKit {

    private static TranslationKit INSTANCE;

    private static final AtomicInteger taskCounter = new AtomicInteger(0);

    public static TranslationKit getInstance() {
        return INSTANCE;
    }

    private ItemStack hoveredStack = null;
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
        translatedResult = I18n.get("misc.translatorpp.translating");
        translationThread = createTranslationThread();
        translationThread.start();
    }

    private Thread createTranslationThread() {
        return new Thread(() -> {
            translatedResult = GoogleTranslationAPITool.getInstance().translate(
                    translatedStack.getHoverName().getString(), TPPConfig.INSTANCE.sourceLanguage.get(), TPPConfig.INSTANCE.targetLanguage.get());
        }, "Translation thread-" + taskCounter.incrementAndGet());
    }

    public void stop() {
        translationThread = null;
        translated = false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void init() {
        TranslatorPP.LOGGER.debug("Initializing TranslationKit");
        INSTANCE = new TranslationKit();
    }

    @SubscribeEvent
    public static void afterScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            if (screen.getSlotUnderMouse() != null && screen.getSlotUnderMouse().hasItem()) {
                INSTANCE.hoveredStack = screen.getSlotUnderMouse().getItem();
            } else {
                INSTANCE.hoveredStack = null;
            }
        }
        if (TPPKeyMappings.TRANSLATE_KEY.isActiveAndMatches(InputConstants.Type.KEYSYM.getOrCreate(event.getKeyCode()))) {
            INSTANCE.translate();
        }
    }

    @SubscribeEvent
    public static void afterScreenKeyReleased(ScreenEvent.KeyReleased.Post event) {
        if (TPPKeyMappings.TRANSLATE_KEY.isActiveAndMatches(InputConstants.Type.KEYSYM.getOrCreate(event.getKeyCode()))) {
            INSTANCE.stop();
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (INSTANCE.translated) {
            event.getToolTip().add(1,
                    Component.translatable("misc.translatorpp.translated_result", INSTANCE.translatedResult));
        }
    }
}
