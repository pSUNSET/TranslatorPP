package net.psunset.translatorpp.compat.clothconfig;

import me.shedaniel.autoconfig.AutoConfig;
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
import net.psunset.translatorpp.compat.clothconfig.gui.TPPConfigClothScreen;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationKit;
import net.psunset.translatorpp.translation.TranslationMode;
import net.psunset.translatorpp.translation.TranslationTool;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The ClothConfig-compatible implementation of {@link TPPConfig}.
 * To get config values, use {@link TPPConfig#getInstance()}.
 */
@ApiStatus.Internal
public class TPPConfigImplCloth implements TPPConfig {
    private static ConfigHolder<General> generalHolder;
    private static ConfigHolder<OpenAI> openaiHolder;

    public TPPConfigImplCloth() {
    }

    @Override
    public TranslationMode getTranslationMode() {
        return general().translationMode;
    }

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

    @Override
    public String getOpenaiCustomBaseUrl() {
        return openai().openaiCustomBaseUrl;
    }

    static General general() {
        return generalHolder.getConfig();
    }

    static OpenAI openai() {
        return openaiHolder.getConfig();
    }

    public static TPPClothConfigData[] configs() {
        return new TPPClothConfigData[]{general(), openai()};
    }

    @Environment(EnvType.CLIENT)
    public static void init() {
        generalHolder = AutoConfig.register(General.class, Toml4jConfigSerializer::new);
        openaiHolder = AutoConfig.register(OpenAI.class, Toml4jConfigSerializer::new);

        ClientTickCallbacks.POST.register(client -> {
            if (TPPKeyMappings.CLOTH_CONFIG_KEY.isDown()) {
                client.setScreen(new TPPConfigClothScreen(client.screen));
            }
        });

        TranslationKit.getInstance().refreshOpenAIClientTool();
        OpenAIClientTool.refreshCacheModels();
    }

    @Config(name = TranslatorPP.ID + "-general")
    private static class General implements TPPClothConfigData {
        private TranslationMode translationMode = Default.translationMode;
        private String sourceLanguage = Default.sourceLanguage;
        private String targetLanguage = Default.targetLanguage;
        private TranslationTool.Type translationTool = Default.translationTool;
        private String openaiModel = Default.openaiModel;

        @Override
        public Screen createScreen(Screen parent) {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> {
                        generalHolder.save();
                        openaiHolder.save();
                        TranslationKit.getInstance().refreshOpenAIClientTool();
                        TranslationKit.getInstance().clearCache();
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

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_mode"), TranslationMode.class, this.translationMode)
                    .setTooltip(Component.translatable("config.translatorpp.translation_mode.tooltip"))
                    .setEnumNameProvider(e -> ((TranslationMode) e).toComponent())
                    .setDefaultValue(Default.translationMode)
                    .setSaveConsumer(it -> this.translationMode = it)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), this.sourceLanguage)
                    .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                    .setSelections(slList)
                    .setDefaultValue(Default.sourceLanguage)
                    .setSaveConsumer(it -> this.sourceLanguage = it)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), this.targetLanguage)
                    .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                    .setSelections(tlList)
                    .setDefaultValue(Default.targetLanguage)
                    .setSaveConsumer(it -> this.targetLanguage = it)
                    .build());

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_tool"), TranslationTool.Type.class, this.translationTool)
                    .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                    .setEnumNameProvider(e -> ((TranslationTool.Type) e).toComponent())
                    .setDefaultValue(Default.translationTool)
                    .setSaveConsumer(it -> this.translationTool = it)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), this.openaiModel)
                    .setSelections(OpenAIClientTool.getCacheModels())
                    .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                    .setDefaultValue(Default.openaiModel)
                    .setSaveConsumer(it -> this.openaiModel = it)
                    .build());

            return builder.build();
        }
    }

    @Config(name = TranslatorPP.ID + "-openai")
    private static class OpenAI implements TPPClothConfigData {
        private String openaiApiKey = Default.openaiApiKey;
        private OpenAIClientTool.Api openaiBaseUrl = Default.openaiBaseUrl;
        private String openaiCustomBaseUrl = Default.openaiCustomBaseUrl;

        @Override
        public Screen createScreen(Screen parent) {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> {
                        generalHolder.save();
                        openaiHolder.save();
                        TranslationKit.getInstance().refreshOpenAIClientTool();
                        OpenAIClientTool.refreshCacheModels();
                    })
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            // The translation of the component doesn't exist because it's completely unaccessible.
            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.default"));

            category.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), this.openaiApiKey)
                    .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                    .setDefaultValue(Default.openaiApiKey)
                    .setSaveConsumer(it -> this.openaiApiKey = it)
                    .build());

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientTool.Api.class, this.openaiBaseUrl)
                    .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                    .setEnumNameProvider(e -> ((OpenAIClientTool.Api) e).toComponent())
                    .setDefaultValue(Default.openaiBaseUrl)
                    .setSaveConsumer(it -> this.openaiBaseUrl = it)
                    .build());

            category.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_custom_baseurl"), this.openaiCustomBaseUrl)
                    .setTooltip(Component.translatable("config.translatorpp.openai_custom_baseurl.tooltip"))
                    .setDefaultValue(Default.openaiCustomBaseUrl)
                    .setSaveConsumer(it -> this.openaiCustomBaseUrl = it)
                    .build());

            return builder.build();
        }
    }
}