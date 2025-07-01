package net.psunset.translatorpp.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.psunset.translatorpp.translation.TranslationKit;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.impl.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class TPPCompatJade_18 {
    public static void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((box, accessor) -> {
            if (Minecraft.getInstance().screen == null) {
                List<FormattedText> hoveredTexts = new ArrayList<>(1); // TODO: Remove initial capacity

                outer:
                for (Tooltip.Line line : ((snownee.jade.impl.Tooltip) box.getTooltip()).lines) {
                    for (var element : line.sortedElements()) {
                        if (element instanceof snownee.jade.impl.ui.TextElement textElement) {
                            hoveredTexts.add(textElement.text);
                            break outer; // TODO: Make it continuously collects texts
                        }
                    }
                }
                setHoveredText(TranslationKit.getInstance(), hoveredTexts);

                if (TranslationKit.getInstance().isTranslated() &&
                        TranslationKit.getInstance().getTranslatedResult() != null &&
                        getCombinedTooltipTexts(hoveredTexts).equals(TranslationKit.getInstance().getTranslatedText())) {
                    addResultToTooltip(TranslationKit.getInstance(), box.getTooltip());
                }
            }
        });
    }

    public static void addResultToTooltip(TranslationKit kit, ITooltip tooltip) {
        Style appliedStyle = Style.EMPTY;

        switch (kit.getTranslatedResult().substring(kit.getTranslatedResult().length() - 3)) {
            case TranslationKit.PROCESSING -> appliedStyle = appliedStyle.withColor(ChatFormatting.DARK_GRAY);
            case TranslationKit.ERROR -> appliedStyle = appliedStyle.withColor(ChatFormatting.RED);
            default -> appliedStyle = appliedStyle.withColor(ChatFormatting.GRAY); // SUCCESS
        }

        String combinedText = kit.getTranslatedResult().substring(0, kit.getTranslatedResult().length() - 3);
        String[] texts = combinedText.split(TranslationKit.COMPONENT_SEP);

        // TODO: Make Jade HUD can adapt to TranslationMode
        tooltip.add(1, Component.literal(texts[0]).withStyle(appliedStyle));
//        switch (TPPConfig.getInstance().getTranslationMode()) {
//            case NAME_ONLY -> {
//                tooltip.add(1, ElementHelper.INSTANCE.text(Component.literal(texts[0]).withStyle(appliedStyle)));
//            }
//            case NAME_TOP -> {
//                tooltip.add(1, Component.literal(texts[0]).withStyle(appliedStyle));
//                if (texts.length > 1) {
//                    // 15 < ${max_length_of_lines} < 30
//                    tooltip.add(Component.literal("-".repeat(Math.min(30, Math.max(15, Arrays.stream(texts).map(String::length).flatMapToInt(IntStream::of).max().getAsInt())))).withStyle(ChatFormatting.DARK_GRAY));
//                    for (int i = 1; i < texts.length; i++) {
//                        tooltip.add(Component.literal(texts[i]).withStyle(appliedStyle));
//                    }
//                }
//            }
//            case ALL_IN_END -> {
//                // 15 < ${max_length_of_lines} < 30
//                tooltip.add(Component.literal("-".repeat(Math.min(30, Math.max(15, Arrays.stream(texts).map(String::length).flatMapToInt(IntStream::of).max().getAsInt())))).withStyle(ChatFormatting.DARK_GRAY));
//                for (String text : texts) {
//                    tooltip.add(Component.literal(text).withStyle(appliedStyle));
//                }
//            }
//            case LINE_BY_LINE -> {
//                for (int i = 0; i < texts.length; i++) {
//                    tooltip.add(i * 2 + 1, Component.literal(texts[i]).withStyle(appliedStyle));
//                }
//            }
//        }
    }

    public static void setHoveredText(TranslationKit kit, List<FormattedText> tooltip) {
        if (tooltip.isEmpty()) {
            kit.setHoveredText((String) null);
            return;
        }
        kit.setHoveredText(getCombinedTooltipTexts(tooltip));
    }

    public static List<String> getTooltipTexts(List<FormattedText> tooltip) {
        return tooltip.stream().map(FormattedText::getString).toList();
    }

    public static String getCombinedTooltipTexts(List<FormattedText> tooltip) {
        return String.join(TranslationKit.COMPONENT_SEP, getTooltipTexts(tooltip));
    }
}
