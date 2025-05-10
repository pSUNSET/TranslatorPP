package net.psunset.translatorpp.compat.clothconfig;

import dev.architectury.event.events.client.ClientTickEvent;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationKit;
import net.psunset.translatorpp.translation.TranslationTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TPPConfigImplCloth implements TPPConfig {
    private static ConfigHolder<General> generalHolder;
    private static ConfigHolder<OpenAI> openaiHolder;

    @Override
    public String getSourceLanguage() {
        return general().sourceLanguage;
    }

    @Override
    public String getTargetLanguage() {
        return general().targetLanguage;
    }

    @Override
    public TranslationTool.Type getTranslationTool() {
        return general().translationTool;
    }

    @Override
    public String getOpenaiModel() {
        return general().openaiModel;
    }

    @Override
    public String getOpenaiApiKey() {
        return openai().openaiApiKey;
    }

    @Override
    public OpenAIClientTool.Api getOpenaiBaseUrl() {
        return openai().openaiBaseUrl;
    }

    public static General general() {
        return generalHolder.getConfig();
    }

    public static OpenAI openai() {
        return openaiHolder.getConfig();
    }

    @Config(name = TranslatorPP.ID + "-general")
    public static class General implements ConfigData {
        private String sourceLanguage = "auto";
        private String targetLanguage = "ja-JP";
        private TranslationTool.Type translationTool = TranslationTool.Type.GoogleTranslation;
        private String openaiModel = "gpt-4o-mini";
    }

    @Config(name = TranslatorPP.ID + "-openai")
    public static class OpenAI implements ConfigData {
        private String openaiApiKey = "";
        private OpenAIClientTool.Api openaiBaseUrl = OpenAIClientTool.Api.OpenAI;
    }

    @Environment(EnvType.CLIENT)
    public static void init() {
        generalHolder = AutoConfig.register(General.class, Toml4jConfigSerializer::new);
        openaiHolder = AutoConfig.register(OpenAI.class, Toml4jConfigSerializer::new);

        ClientTickEvent.CLIENT_POST.register(client -> {
            if (TPPKeyMappings.CLOTH_CONFIG_KEY.isDown()) {
                client.setScreen(TPPConfigImplCloth.createScreen(client.screen));
            }
        });

        TranslationKit.getInstance().refreshOpenAIClientTool();
    }

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable(() -> {
                    generalHolder.save();
                    openaiHolder.save();
                    TranslationKit.getInstance().clearCache();
                    TranslationKit.getInstance().refreshOpenAIClientTool();
                })
                .setTitle(Component.translatable("config.title.translatorpp"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.general"));
        ConfigCategory openaiCategory = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.openai"));

        List<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag)
                .distinct()
                .sorted(String::compareTo)
                .toList();

        List<String> slList = new ArrayList<>(tlList.size() + 1);
        slList.add("auto");
        slList.addAll(tlList);

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), general().sourceLanguage)
                .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                .setSelections(slList)
                .setDefaultValue("auto")
                .setSaveConsumer(it -> general().sourceLanguage = it)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), general().targetLanguage)
                .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                .setSelections(tlList)
                .setDefaultValue("es-ES")
                .setSaveConsumer(it -> general().targetLanguage = it)
                .build());

        generalCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_tool"), TranslationTool.Type.class, general().translationTool)
                .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                .setDefaultValue(TranslationTool.Type.GoogleTranslation)
                .setSaveConsumer(it -> general().translationTool = it)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), general().openaiModel)
                .setSelections(OpenAIClientTool.getInstance().getModelOffline())
                .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                .setDefaultValue(OpenAIClientTool.Api.OpenAI.defaultModel)
                .setSaveConsumer(it -> general().openaiModel = it)
                .build());

        openaiCategory.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), openai().openaiApiKey)
                .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                .setDefaultValue("")
                .setSaveConsumer(it -> openai().openaiApiKey = it)
                .build());

        openaiCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientTool.Api.class, openai().openaiBaseUrl)
                .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                .setDefaultValue(OpenAIClientTool.Api.OpenAI)
                .setSaveConsumer(it -> openai().openaiBaseUrl = it)
                .build());

        return builder.build();
    }
}