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
        return config().source_language;
    }

    @Override
    public String getTargetLanguage() {
        return config().target_language;
    }

    @Override
    public TranslationService getService() {
        return config().service;
    }

    @Override
    public String getOpenaiApiKey() {
        return config().openai_apikey;
    }

    @Override
    public OpenAIClientProvider.Api getOpenaiBaseUrl() {
        return config().openai_baseurl;
    }

    @Override
    public String getOpenaiCustomBaseUrl() {
        return config().openai_custom_baseurl;
    }

    @Override
    public String getOpenaiModel() {
        return config().openai_model;
    }

    @Override
    public String getDeepLApiKey() {
        return config().deepl_apikey;
    }

    @Override
    public String getLibreApiKey() {
        return config().libre_apikey;
    }

    @Override
    public String getLibreBaseUrl() {
        return config().libre_baseurl;
    }

    @Override
    public String getOllamaBaseUrl() {
        return config().ollama_baseurl;
    }

    @Override
    public String getOllamaModel() {
        return config().ollama_model;
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
        private String source_language = Default.sourceLanguage;
        private String target_language = Default.targetLanguage;
        private TranslationService service = Default.service;

        /* OpenAI */
        private String openai_apikey = Default.openaiApiKey;
        private OpenAIClientProvider.Api openai_baseurl = Default.openaiBaseUrl;
        private String openai_custom_baseurl = Default.openaiCustomBaseUrl;
        private String openai_model = Default.openaiModel;

        /* DeepL */
        private String deepl_apikey = Default.deeplApiKey;

        /* Libre */
        private String libre_apikey = Default.libreApiKey;
        private String libre_baseurl = Default.libreBaseUrl;

        /* Ollama */
        private String ollama_baseurl = Default.ollamaBaseUrl;
        private String ollama_model = Default.ollamaModel;

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

            general.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), this.source_language)
                    .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                    .setSelections(slList)
                    .setDefaultValue(Default.sourceLanguage)
                    .setSaveConsumer(it -> {
                        if (!this.source_language.equals(it)) {
                            this.source_language = it;
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            general.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), this.target_language)
                    .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                    .setSelections(tlList)
                    .setDefaultValue(Default.targetLanguage)
                    .setSaveConsumer(it -> {
                        if (!this.target_language.equals(it)) {
                            this.target_language = it;
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

            openai.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), this.openai_apikey)
                    .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                    .setDefaultValue(Default.openaiApiKey)
                    .setSaveConsumer(it -> {
                        if (!this.openai_apikey.equals(it)) {
                            this.openai_apikey = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.refreshOpenaiCacheModelsLater();
                        }
                    })
                    .build());

            openai.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientProvider.Api.class, this.openai_baseurl)
                    .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(Default.openaiBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.openai_baseurl.equals(it)) {
                            this.openai_baseurl = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.refreshOpenaiCacheModelsLater();
                        }
                    })
                    .build());

            openai.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_custom_baseurl"), this.openai_custom_baseurl)
                    .setTooltip(Component.translatable("config.translatorpp.openai_custom_baseurl.tooltip"))
                    .setDefaultValue(Default.openaiCustomBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.openai_custom_baseurl.equals(it)) {
                            this.openai_custom_baseurl = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.refreshOpenaiCacheModelsLater();
                        }
                    })
                    .build());

            openai.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), this.openai_model)
                    .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                    .setSelections(OpenAIClientProvider.getCacheModels())
                    .setDefaultValue(Default.openaiModel)
                    .setSaveConsumer(it -> {
                        if (!this.openai_model.equals(it)) {
                            this.openai_model = it;
                            OnSaveManager.refreshOpenaiLater();
                            OnSaveManager.clearTranslationCachesLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            deepl.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.deepl_apikey"), this.deepl_apikey)
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.tooltip"))
                    .setDefaultValue(Default.deeplApiKey)
                    .setSaveConsumer(it -> {
                        if (!this.deepl_apikey.equals(it)) {
                            this.deepl_apikey = it;
                            OnSaveManager.refreshDeeplLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            libre.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.libre_apikey"), this.libre_apikey)
                    .setTooltip(Component.translatable("config.translatorpp.libre_apikey.tooltip"))
                    .setDefaultValue(Default.libreApiKey)
                    .setSaveConsumer(it -> {
                        if (!this.libre_apikey.equals(it)) {
                            this.libre_apikey = it;
                            OnSaveManager.refreshLibreLater();
                        }
                    })
                    .build());

            libre.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.libre_baseurl"), this.libre_baseurl)
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.libre_baseurl"))
                    .setDefaultValue(Default.libreBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.libre_baseurl.equals(it)) {
                            this.libre_baseurl = it;
                            OnSaveManager.refreshLibreLater();
                        }
                    })
                    .build());

            /* ---------------------------------------- */

            ollama.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.ollama_baseurl"), this.ollama_baseurl)
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.ollama_baseurl"))
                    .setDefaultValue(Default.ollamaBaseUrl)
                    .setSaveConsumer(it -> {
                        if (!this.ollama_baseurl.equals(it)) {
                            this.ollama_baseurl = it;
                            OnSaveManager.refreshOllamaLater();
                            OnSaveManager.refreshOllamaCacheModelsLater();
                        }
                    })
                    .build());

            ollama.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.ollama_model"), this.ollama_model)
                    .setTooltip(Component.translatable("config.translatorpp.ollama_model.tooltip"))
                    .setSelections(OllamaClientProvider.getCacheModels())
                    .setDefaultValue(Default.ollamaModel)
                    .setSaveConsumer(it -> {
                        if (!this.ollama_model.equals(it)) {
                            this.ollama_model = it;
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