package net.psunset.translatorpp.neoforge.translation;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.TranslationKit;

@EventBusSubscriber(modid = TranslatorPP.ID, value = Dist.CLIENT)
public class TranslationKitNeoForge {

    @SubscribeEvent
    public static void afterScreenKeyPressed(ScreenEvent.KeyPressed.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
            if (screen.getSlotUnderMouse() != null && screen.getSlotUnderMouse().hasItem()) {
                TranslationKit.getInstance().setHoveredStack(screen.getSlotUnderMouse().getItem());
            }

            if (TPPKeyMappings.TRANSLATE_KEY.isActiveAndMatches(InputConstants.Type.KEYSYM.getOrCreate(event.getKeyCode()))) {
                TranslationKit.getInstance().start(screen.getMinecraft());
            }
        }
    }


    @SubscribeEvent
    public static void afterScreenKeyReleased(ScreenEvent.KeyReleased.Post event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?>) {
            if (TPPKeyMappings.TRANSLATE_KEY.isActiveAndMatches(InputConstants.Type.KEYSYM.getOrCreate(event.getKeyCode()))) {
                TranslationKit.getInstance().stop();
            }
        }
    }

    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof AbstractContainerScreen<?>) {
            TranslationKit.getInstance().stop();
        }
    }
}
