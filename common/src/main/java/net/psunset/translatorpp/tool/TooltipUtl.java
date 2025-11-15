package net.psunset.translatorpp.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.psunset.translatorpp.translation.TranslationKit;

import java.util.List;

public final class TooltipUtl {
    public static List<Component> getTooltipComponents(ItemStack stack, Minecraft client) {
        return stack.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL);
    }

    public static List<String> getTooltipTexts(ItemStack stack, Minecraft client) {
        return getTooltipComponents(stack, client).stream().map(Component::getString).toList();
    }

    public static String getCombinedTooltipText(ItemStack stack, Minecraft client) {
        return String.join(TranslationKit.SEPARATOR, getTooltipTexts(stack, client));
    }

    public static List<String> getTooltipTexts(List<Component> tooltip) {
        return tooltip.stream().map(Component::getString).toList();
    }

    public static String getCombinedTooltipText(List<Component> tooltip) {
        return String.join(TranslationKit.SEPARATOR, getTooltipTexts(tooltip));
    }
}
