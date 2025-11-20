package net.psunset.translatorpp.event.neoforge;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.event.ItemTooltipCallbacks;
import net.psunset.translatorpp.event.ScreenCallbacks;

public final class TPPEventsImpl {

    public static void init(IEventBus gameBus, IEventBus modBus) {
        ClientTickCallbacks.POST.merge();
        gameBus.addListener(ClientTickEvent.Post.class, event -> ClientTickCallbacks.POST.getInvoker().afterTick(Minecraft.getInstance()));

        ItemTooltipCallbacks.EVENT.merge();
        gameBus.addListener(ItemTooltipEvent.class, event -> ItemTooltipCallbacks.EVENT.getInvoker().getTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()));

        ScreenCallbacks.KEY_PRESSED_POST.merge();
        gameBus.addListener(ScreenEvent.KeyPressed.Post.class, event -> ScreenCallbacks.KEY_PRESSED_POST.getInvoker().afterKeyPress(event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers()));

        ScreenCallbacks.KEY_RELEASED_POST.merge();
        gameBus.addListener(ScreenEvent.KeyReleased.Post.class, event -> ScreenCallbacks.KEY_RELEASED_POST.getInvoker().afterKeyRelease(event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers()));

        ScreenCallbacks.REMOVED.merge();
        gameBus.addListener(ScreenEvent.Closing.class, event -> ScreenCallbacks.REMOVED.getInvoker().onRemove(event.getScreen()));
    }
}
