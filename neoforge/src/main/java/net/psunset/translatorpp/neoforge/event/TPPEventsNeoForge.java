package net.psunset.translatorpp.neoforge.event;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.psunset.translatorpp.event.TPPEvents;

public class TPPEventsNeoForge {

    public static void init(IEventBus gameBus, IEventBus modBus) {
        var mergedClientTickPost = TPPEvents.CLIENT_TICK_POST.merge();
        gameBus.addListener(ClientTickEvent.Post.class, event -> mergedClientTickPost.afterTick(Minecraft.getInstance()));

        var mergedItemTooltip = TPPEvents.ITEM_TOOLTIP.merge();
        gameBus.addListener(ItemTooltipEvent.class, event -> mergedItemTooltip.getTooltip(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip()));

        var mergedScreenKeyPressedPost = TPPEvents.SCREEN_KEY_PRESSED_POST.merge();
        gameBus.addListener(ScreenEvent.KeyPressed.Post.class, event -> mergedScreenKeyPressedPost.afterKeyPress(event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers()));

        var mergedScreenKeyReleasedPost = TPPEvents.SCREEN_KEY_RELEASED_POST.merge();
        gameBus.addListener(ScreenEvent.KeyReleased.Post.class, event -> mergedScreenKeyReleasedPost.afterKeyRelease(event.getScreen(), event.getKeyCode(), event.getScanCode(), event.getModifiers()));

        var mergedScreenRemoved = TPPEvents.SCREEN_REMOVED.merge();
        gameBus.addListener(ScreenEvent.Closing.class, event -> mergedScreenRemoved.onRemove(event.getScreen()));
    }
}
