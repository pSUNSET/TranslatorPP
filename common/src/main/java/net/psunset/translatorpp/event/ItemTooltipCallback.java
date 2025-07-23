package net.psunset.translatorpp.event;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@FunctionalInterface
public interface ItemTooltipCallback extends TPPEvent.Callback {
    void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, TooltipFlag tooltipType, List<Component> lines);

    static ItemTooltipCallback merge(Iterable<ItemTooltipCallback> callbacks) {
        return (stack, tooltipContext, tooltipType, lines) -> {
            for (var callback : callbacks) {
                callback.getTooltip(stack, tooltipContext, tooltipType, lines);
            }
        };
    }
}
