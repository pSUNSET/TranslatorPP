package net.psunset.translatorpp.event.fabric;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.psunset.translatorpp.command.ClientCommandSourceStack;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.RegisterClientCommandsCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;

public final class TPPEventsImpl {

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickCallbacks.POST.merge()::afterTick);

        ItemTooltipCallback.EVENT.register(ItemTooltipCallbacks.EVENT.merge()::getTooltip);

        RegisterClientCommandsCallbacks.EVENT.merge();
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, context) -> RegisterClientCommandsCallbacks.EVENT.getInvoker().register((CommandDispatcher<ClientCommandSourceStack>) (CommandDispatcher<?>) dispatcher, context)));

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register(ScreenCallbacks.KEY_PRESSED_POST.merge()::afterKeyPress);

            ScreenKeyboardEvents.afterKeyRelease(screen).register(ScreenCallbacks.KEY_RELEASED_POST.merge()::afterKeyRelease);

            ScreenEvents.remove(screen).register(ScreenCallbacks.REMOVED.merge()::onRemove);
        });
    }
}
