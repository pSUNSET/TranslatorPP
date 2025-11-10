package net.psunset.translatorpp.fabric.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;

public final class TPPEventsFabric {

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickCallbacks.POST.merge()::afterTick);

        ItemTooltipCallback.EVENT.register(ItemTooltipCallbacks.EVENT.merge()::getTooltip);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register(ScreenCallbacks.KEY_PRESSED_POST.merge()::afterKeyPress);

            ScreenKeyboardEvents.afterKeyRelease(screen).register(ScreenCallbacks.KEY_RELEASED_POST.merge()::afterKeyRelease);

            ScreenEvents.remove(screen).register(ScreenCallbacks.REMOVED.merge()::onRemove);
        });
    }
}
