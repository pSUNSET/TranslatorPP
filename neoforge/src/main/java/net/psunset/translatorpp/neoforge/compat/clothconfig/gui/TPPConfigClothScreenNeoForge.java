package net.psunset.translatorpp.neoforge.compat.clothconfig.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.psunset.translatorpp.compat.clothconfig.gui.TPPConfigClothScreen;
import net.psunset.translatorpp.gui.ScreenProvider;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.neoforge.config.TPPConfigImplNeoForge;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TPPConfigClothScreenNeoForge {

    @OnlyIn(Dist.CLIENT)
    public static void init() {
        NeoForge.EVENT_BUS.addListener(TPPConfigClothScreenNeoForge::afterClientTickIfHasClothConfig);
    }

    public static void afterClientTickIfHasClothConfig(ClientTickEvent.Post event) {
        if (TPPKeyMappings.CLOTH_CONFIG_KEY.isDown()) {
            Minecraft.getInstance().setScreen(create(Minecraft.getInstance().screen));
        }
    }

    public static TPPConfigClothScreen create(Screen parent) {
        return new TPPConfigClothScreen(parent,
                List.of(General.INSTANCE, OpenAI.INSTANCE));
    }

    /**
     * An edition of {@link net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth.General}
     */
    private static class General implements ScreenProvider {

        public static General INSTANCE = new General();

        @Override
        public Screen createScreen(Screen parent) {

            TPPConfigImplNeoForge.General config = TPPConfigImplNeoForge.GENERAL;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> {
                        TPPConfigImplNeoForge.generalSpec.save();
                    })
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            // The translation of the component doesn't exist because it's completely unaccessible.
            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.default"));

            List<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::toLanguageTag)
                    .distinct()
                    .sorted(String::compareTo)
                    .toList();

            List<String> slList = new ArrayList<>(tlList.size() + 1);
            slList.add("auto");
            slList.addAll(tlList);

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), config.sourceLanguage.get())
                    .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                    .setSelections(slList)
                    .setDefaultValue("auto")
                    .setSaveConsumer(config.sourceLanguage::set)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), config.targetLanguage.get())
                    .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                    .setSelections(tlList)
                    .setDefaultValue("ja-JP")
                    .setSaveConsumer(config.targetLanguage::set)
                    .build());

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_tool"), TranslationTool.Type.class, config.translationTool.get())
                    .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                    .setDefaultValue(TranslationTool.Type.GoogleTranslation)
                    .setSaveConsumer(config.translationTool::set)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), config.openaiModel.get())
                    .setSelections(OpenAIClientTool.getCacheModels())
                    .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                    .setDefaultValue("")
                    .setSaveConsumer(config.openaiModel::set)
                    .build());

            return builder.build();
        }
    }

    /**
     * An edition of {@link net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth.OpenAI}
     */
    private static class OpenAI implements ScreenProvider {

        public static OpenAI INSTANCE = new OpenAI();

        @Override
        public Screen createScreen(Screen parent) {

            TPPConfigImplNeoForge.OpenAI config = TPPConfigImplNeoForge.OPENAI;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> {
                        TPPConfigImplNeoForge.openaiSpec.save();
                    })
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            // The translation of the component doesn't exist because it's completely unaccessible.
            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.default"));

            category.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), config.openaiApiKey.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                    .setDefaultValue("")
                    .setSaveConsumer(config.openaiApiKey::set)
                    .build());

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientTool.Api.class, config.openaiBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                    .setDefaultValue(OpenAIClientTool.Api.OpenAI)
                    .setSaveConsumer(config.openaiBaseUrl::set)
                    .build());

            return builder.build();
        }
    }
}
