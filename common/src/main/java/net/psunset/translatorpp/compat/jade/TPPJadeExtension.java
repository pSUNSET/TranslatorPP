package net.psunset.translatorpp.compat.jade;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.psunset.translatorpp.tool.RLUtl;
import net.psunset.translatorpp.tool.TooltipUtl;
import net.psunset.translatorpp.translation.TranslationKit;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.TextElement;
import snownee.jade.impl.Tooltip;

import java.util.ArrayList;
import java.util.List;

public final class TPPJadeExtension {

    static final IBlockComponentProvider BLOCK = new Block();
    private static final ResourceLocation BLOCK_LOC = RLUtl.of("block_translation");
    static final IEntityComponentProvider ENTITY = new Entity();
    private static final ResourceLocation ENTITY_LOC = RLUtl.of("entity_translation");

    public static final class Block implements IBlockComponentProvider {

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            tooltipCallback((Tooltip) tooltip);
        }

        @Override
        public ResourceLocation getUid() {
            return BLOCK_LOC;
        }
    }

    public static final class Entity implements IEntityComponentProvider {

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            tooltipCallback((Tooltip) tooltip);
        }

        @Override
        public ResourceLocation getUid() {
            return ENTITY_LOC;
        }
    }

    private static void tooltipCallback(Tooltip tooltip) {
        if (Minecraft.getInstance().screen == null) {
            List<Component> hoveredTexts = new ArrayList<>(1); // TODO: Remove initial capacity

            outer:
            for (var line : tooltip.lines) {
                for (var element : line.elements()) {
                    if (element instanceof TextElement textElement) {
                        hoveredTexts.add(textElement.getNarration());
                        break outer; // TODO: Make it continuously collects texts
                    }
                }
            }
            TranslationKit.getInstance().setHoveredText(hoveredTexts);

            if (TranslationKit.getInstance().isTranslated() &&
                    TranslationKit.getInstance().getTranslatedResult() != null &&
                    TooltipUtl.getCombinedTooltipText(hoveredTexts).equals(TranslationKit.getInstance().getTranslatedText())) {
                addResultToTooltip(TranslationKit.getInstance(), tooltip);
            }
        }
    }

    private static void addResultToTooltip(TranslationKit kit, Tooltip tooltip) {
        var styledResult = kit.getStyledResultLines();
        Style appliedStyle = styledResult.getLeft();
        String[] texts = styledResult.getRight();

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
}
