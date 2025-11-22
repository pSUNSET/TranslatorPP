package net.psunset.translatorpp.event.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;

public final class TPPEventsImpl {

    public static void init(IEventBus gameBus, IEventBus modBus) {
        ClientTickCallbacks.POST.merge();
        gameBus.<TickEvent.ClientTickEvent>addListener(event -> {
            if (event.phase == TickEvent.Phase.END) {
                ClientTickCallbacks.POST.getInvoker().afterTick(Minecraft.getInstance());
            }
        });

        ItemTooltipCallbacks.EVENT.merge();
        gameBus.<ItemTooltipEvent>addListener(event -> ItemTooltipCallbacks.EVENT.getInvoker().getTooltip(event.getItemStack(), event.getFlags(), event.getToolTip()));

        ScreenCallbacks.KEY_PRESSED_POST.merge();
        gameBus.<ScreenEvent.KeyPressed.Post>addListener(event -> ScreenCallbacks.KEY_PRESSED_POST.getInvoker().afterKeyPress(event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers()));

        ScreenCallbacks.KEY_RELEASED_POST.merge();
        gameBus.<ScreenEvent.KeyReleased.Post>addListener(event -> ScreenCallbacks.KEY_RELEASED_POST.getInvoker().afterKeyRelease(event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers()));

        ScreenCallbacks.REMOVED.merge();
        gameBus.<ScreenEvent.Closing>addListener(event -> ScreenCallbacks.REMOVED.getInvoker().onRemove(event.getScreen()));
    }
}
