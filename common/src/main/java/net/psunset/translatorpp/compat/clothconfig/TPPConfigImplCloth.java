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
import net.psunset.translatorpp.api.ComponentizableEnum;
import net.psunset.translatorpp.api.ITPPClothConfigData;
import net.psunset.translatorpp.compat.clothconfig.gui.TPPConfigClothScreenWrapper;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.core.*;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
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
    private static ConfigHolder<TPPConfigData> holder;

    public TPPConfigImplCloth() {
    }

    @Override
    public TranslationMode getMode() {
        return config().mode;
    }

    @Override
    public String getSourceLanguage() {
        return config().sourceLanguage;
    }

    @Override
    public String getTargetLanguage() {
        return config().targetLanguage;
    }

    @Override
    public TranslationService getService() {
        return config().service;
    }

    @Override
    public String getOpenaiApiKey() {
        return config().openaiApiKey;
    }

    @Override
    public OpenAIClientProvider.Api getOpenaiBaseUrl() {
        return config().openaiBaseUrl;
    }

    @Override
    public String getOpenaiCustomBaseUrl() {
        return config().openaiCustomBaseUrl;
    }

    @Override
    public String getOpenaiModel() {
        return config().openaiModel;
    }

    @Override
    public String getDeepLApiKey() {
        return config().deeplApiKey;
    }

    @Override
    public String getLibreApiKey() {
        return config().libreApiKey;
    }

    @Override
    public String getLibreBaseUrl() {
        return config().libreBaseUrl;
    }

    @Override
    public String getOllamaBaseUrl() {
        return config().ollamaBaseUrl;
    }

    @Override
    public String getOllamaModel() {
        return config().ollamaModel;
    }

    public static TPPConfigData config() {
        return holder.getConfig();
    }

    @Deprecated
    public static ITPPClothConfigData[] configs() {
        return new ITPPClothConfigData[]{config()};
    }

    public static void init() {
        holder = AutoConfig.register(TPPConfigData.class, Toml4jConfigSerializer::new);

        ClientTickCallbacks.POST.register(client -> {
            if (TPPKeyMappings.CONFIG_KEY.isDown()) {
                client.setScreen(TPPConfigClothScreenWrapper.createScreen(client.screen));
            }
        });

        OpenAIClientProvider.getInstance().refresh();
        OpenAIClientProvider.refreshCacheModels();
        DeepLTranslationProvider.getInstance().refresh();
        LibreTranslateProvider.getInstance().refresh();
        OllamaClientProvider.getInstance().refresh();
        OllamaClientProvider.refreshCacheModels();
    }

    @Config(name = TranslatorPP.ID)
    public static final class TPPConfigData implements ITPPClothConfigData {

        /* General */
        private TranslationMode mode = Default.mode;
        private String sourceLanguage = Default.sourceLanguage;
        private String targetLanguage = Default.targetLanguage;
        private TranslationService service = Default.service;

        /* OpenAI */
        private String openaiApiKey = Default.openaiApiKey;
        private OpenAIClientProvider.Api openaiBaseUrl = Default.openaiBaseUrl;
        private String openaiCustomBaseUrl = Default.openaiCustomBaseUrl;
        private String openaiModel = Default.openaiModel;

        /* DeepL */
        private String deeplApiKey = Default.deeplApiKey;

        /* Libre */
        private String libreApiKey = Default.libreApiKey;
        private String libreBaseUrl = Default.libreBaseUrl;

        /* Ollama */
        private String ollamaBaseUrl = Default.ollamaBaseUrl;
        private String ollamaModel = Default.ollamaModel;

        @Override
        public Screen createScreen(Screen parent) {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(OnSaveManager::fire)
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.general"));
            ConfigCategory openai = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.openai"));
            ConfigCategory deepl = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.deepl"));
            ConfigCategory libre = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.libre"));
            ConfigCategory ollama = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.ollama"));

            List<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::toLanguageTag)
                    .distinct()
                    .sorted(String::compareTo)
                    .toList();

            List<String> slList = new ArrayList<>(tlList.size() + 1);
            slList.add("auto");
            slList.addAll(tlList);

            general.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.mode"), TranslationMode.class, this.mode)
                    .setTooltip(Component.translatable("config.translatorpp.mode.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(Default.mode)
                    .setSaveConsumer(it -> {
                        if (this.mode != it) {
                            this.mode = it;
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            general.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), this.sourceLanguage)
                    .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                    .setSelections(slList)
                    .setDefaultValue(Default.sourceLanguage)
                    .setSaveConsumer(it -> {
                        if (!this.sourceLanguage.equals(it)) {
                            this.sourceLanguage = it;
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            general.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), this.targetLanguage)
                    .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                    .setSelections(tlList)
                    .setDefaultValue(Default.targetLanguage)
                    .setSaveConsumer(it -> {
                        if (!this.targetLanguage.equals(it)) {
                            this.targetLanguage = it;
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.service"), TranslationService.class, this.service)
                    .setTooltip(Component.translatable("config.translatorpp.service.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(Default.service)
                    .setSaveConsumer(it -> {
                        if (this.service != it) {
                            this.service = it;
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            openai.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), this.openaiApiKey)
                    .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                    .setDefaultValue(Default.openaiApiKey)
                    .setSaveConsumer(it -> {
                        if (!this.openaiApiKey.equals(it)) {
                            this.openaiApiKey = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.refreshOpenaiCacheModelsLater();
                        }
                    })
                    .build());

            openai.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientProvider.Api.class, this.openaiBaseUrl)
                    .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(Default.openaiBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.openaiBaseUrl.equals(it)) {
                            this.openaiBaseUrl = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.refreshOpenaiCacheModelsLater();
                        }
                    })
                    .build());

            openai.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_custom_baseurl"), this.openaiCustomBaseUrl)
                    .setTooltip(Component.translatable("config.translatorpp.openai_custom_baseurl.tooltip"))
                    .setDefaultValue(Default.openaiCustomBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.openaiCustomBaseUrl.equals(it)) {
                            this.openaiCustomBaseUrl = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.refreshOpenaiCacheModelsLater();
                        }
                    })
                    .build());

            openai.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), this.openaiModel)
                    .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                    .setSelections(OpenAIClientProvider.getCacheModels())
                    .setDefaultValue(Default.openaiModel)
                    .setSaveConsumer(it -> {
                        if (!this.openaiModel.equals(it)) {
                            this.openaiModel = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            deepl.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.deepl_apikey"), this.deeplApiKey)
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.tooltip"))
                    .setDefaultValue(Default.deeplApiKey)
                    .setSaveConsumer(it -> {
                        if (!this.deeplApiKey.equals(it)) {
                            this.deeplApiKey = it;
                            OnSaveManager.refreshDeeplLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            libre.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.libre_apikey"), this.libreApiKey)
                    .setTooltip(Component.translatable("config.translatorpp.libre_apikey.tooltip"))
                    .setDefaultValue(Default.libreApiKey)
                    .setSaveConsumer(it -> {
                        if (!this.libreApiKey.equals(it)) {
                            this.libreApiKey = it;
                            OnSaveManager.refreshLibreLater();
                        }
                    })
                    .build());

            libre.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.libre_baseurl"), this.libreBaseUrl)
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.libre_baseurl"))
                    .setDefaultValue(Default.libreBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.libreBaseUrl.equals(it)) {
                            this.libreBaseUrl = it;
                            OnSaveManager.refreshLibreLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            ollama.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.ollama_baseurl"), this.ollamaBaseUrl)
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.ollama_baseurl"))
                    .setDefaultValue(Default.ollamaBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.ollamaBaseUrl.equals(it)) {
                            this.ollamaBaseUrl = it;
                            OnSaveManager.refreshOllamaLater();
                            OnSaveManager.refreshOllamaCacheModelsLater();
                        }
                    })
                    .build());

            ollama.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.ollama_model"), this.ollamaModel)
                    .setTooltip(Component.translatable("config.translatorpp.ollama_model.tooltip"))
                    .setSelections(OllamaClientProvider.getCacheModels())
                    .setDefaultValue(Default.ollamaModel)
                    .setSaveConsumer(it -> {
                        if (!this.ollamaModel.equals(it)) {
                            this.ollamaModel = it;
                            OnSaveManager.refreshOllamaLater();
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());


            return builder.build();
        }
    }

    private static final class OnSaveManager {
        private static boolean shouldClearTranslationCaches = false;
        private static boolean shouldRefreshOpenai = false;
        private static boolean shouldRefreshOpenaiCacheModels = false;
        private static boolean shouldRefreshDeepl = false;
        private static boolean shouldRefreshLibre = false;
        private static boolean shouldRefreshOllama = false;
        private static boolean shouldRefreshOllamaCacheModels = false;

        private static void clearTranslationCachesLater() {
            shouldClearTranslationCaches = true;
        }

        private static void refreshOpenaiLater() {
            shouldRefreshOpenai = true;
        }

        private static void refreshOpenaiCacheModelsLater() {
            shouldRefreshOpenaiCacheModels = true;
        }

        private static void refreshDeeplLater() {
            shouldRefreshDeepl = true;
        }

        private static void refreshLibreLater() {
            shouldRefreshLibre = true;
        }

        private static void refreshOllamaLater() {
            shouldRefreshOllama = true;
        }

        private static void refreshOllamaCacheModelsLater() {
            shouldRefreshOllamaCacheModels = true;
        }

        private static void fire() {
            holder.save();

            if (shouldClearTranslationCaches) {
                TranslationKit.getInstance().clearCache();
                shouldClearTranslationCaches = false;
            }

            if (shouldRefreshOpenai) {
                OpenAIClientProvider.getInstance().refresh();
                shouldRefreshOpenai = false;
            }

            if (shouldRefreshOpenaiCacheModels) {
                OpenAIClientProvider.refreshCacheModels();
                shouldRefreshOpenaiCacheModels = false;
            }

            if (shouldRefreshDeepl) {
                DeepLTranslationProvider.getInstance().refresh();
                shouldRefreshDeepl = false;
            }

            if (shouldRefreshLibre) {
                DeepLTranslationProvider.getInstance().refresh();
                shouldRefreshLibre = false;
            }

            if (shouldRefreshOllama) {
                OllamaClientProvider.getInstance().refresh();
                shouldRefreshOllama = false;
            }

            if (shouldRefreshOllamaCacheModels) {
                OllamaClientProvider.refreshCacheModels();
                shouldRefreshOllamaCacheModels = false;
            }
        }
    }
}