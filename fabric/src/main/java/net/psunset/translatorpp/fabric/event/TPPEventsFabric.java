package net.psunset.translatorpp.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.psunset.translatorpp.event.TPPEvents;

public class TPPEventsFabric {

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(TPPEvents.CLIENT_TICK_POST.merge()::afterTick);

        ItemTooltipCallback.EVENT.register(TPPEvents.ITEM_TOOLTIP.merge()::getTooltip);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register(TPPEvents.SCREEN_KEY_PRESSED_POST.merge()::afterKeyPress);

            ScreenKeyboardEvents.afterKeyRelease(screen).register(TPPEvents.SCREEN_KEY_RELEASED_POST.merge()::afterKeyRelease);

            ScreenEvents.remove(screen).register(TPPEvents.SCREEN_REMOVED.merge()::onRemove);
        });
    }
}
