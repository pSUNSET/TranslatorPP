package net.psunset.translatorpp.tool;

import com.sun.java.accessibility.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.translation.TranslationKit;

import java.util.List;

public class TooltipUtl {

    public static List<Component> getTooltipComponents(ItemStack stack, Minecraft client) {
        return stack.getTooltipLines(Item.TooltipContext.of(client.level), client.player, TooltipFlag.NORMAL);
    }

    public static List<String> getTooltipTexts(ItemStack stack, Minecraft client) {
        return getTooltipComponents(stack, client).stream().map(Component::getString).toList();
    }

    public static String getCombinedTooltipTexts(ItemStack stack, Minecraft client) {
        TranslatorPP.LOGGER.info(String.join(TranslationKit.COMPONENT_SEP, getTooltipTexts(stack, client)));
        return String.join(TranslationKit.COMPONENT_SEP, getTooltipTexts(stack, client));
    }

    public static List<String> getTooltipTexts(List<Component> tooltip) {
        return tooltip.stream().map(Component::getString).toList();
    }

    public static String getCombinedTooltipTexts(List<Component> tooltip) {
        return String.join(TranslationKit.COMPONENT_SEP, getTooltipTexts(tooltip));
    }
}
