package net.psunset.translatorpp.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@FunctionalInterface
public interface ItemTooltipCallbacks extends Event.Callback {
    void getTooltip(ItemStack stack, TooltipFlag flag, List<Component> lines);

    Event<ItemTooltipCallbacks> EVENT = new Event<>(ItemTooltipCallbacks.class, ItemTooltipCallbacks::merge);

    private static ItemTooltipCallbacks merge(ItemTooltipCallbacks[] callbacks) {
        return (stack, tooltipType, lines) -> {
            for (var callback : callbacks) {
                callback.getTooltip(stack, tooltipType, lines);
            }
        };
    }
}
