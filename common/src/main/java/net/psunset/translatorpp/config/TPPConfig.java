package net.psunset.translatorpp.config;

import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.annotations.ExpectMixin;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import net.psunset.translatorpp.core.OpenAIClientProvider;
import net.psunset.translatorpp.core.TranslationMode;
import net.psunset.translatorpp.core.TranslationService;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.platform.Platform;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.tool.MessageUtl;
import org.jetbrains.annotations.ApiStatus;

/**
 * The main config interface for Translator++.
 * To get config values, use {@link TPPConfig#getInstance()}.
 */
public interface TPPConfig {

    TranslationMode getMode();

    String getSourceLanguage();

    String getTargetLanguage();

    TranslationService getService();

    String getOpenaiApiKey();

    OpenAIClientProvider.Api getOpenaiBaseUrl();

    String getOpenaiCustomBaseUrl();

    String getOpenaiModel();

    String getDeepLApiKey();

    String getLibreApiKey();

    String getLibreBaseUrl();

    String getOllamaBaseUrl();

    String getOllamaModel();

    @ExpectMixin(value = ExpectMixin.Expected.NEOFORGE, method = ExpectMixin.Method.OVERWRITE)
    static void init() {
        if (Platform.isNeoForge()) {
            throw new AssertionError("Mixin missing!");
        } else if (CompatUtl.ClothConfig.isLoaded()) {
            TranslatorPP.LOGGER.debug("Cloth Config is loaded, using cloth config for Translator++ Config.");
            Dummy.INSTANCE = new TPPConfigImplCloth();
            TPPConfigImplCloth.init();
        } else {
            TranslatorPP.LOGGER.debug("No config API is loaded, using default values for Translator++ Config.");
            Dummy.INSTANCE = new Dummy();
            Dummy.init();
        }
    }

    static TPPConfig getInstance() {
        return Dummy.INSTANCE;
    }

    /**
     * The dummy implementation of TPPConfig, which uses default values.
     * Only used when no config API is available.
     */
    @ApiStatus.Internal
    class Dummy implements TPPConfig {

        /**
         * The instance of the TPPConfig, not only works for the dummy one.
         * Modify this field is not allowed.
         * To get this instance, use {@link TPPConfig#getInstance()}.
         */
        public static TPPConfig INSTANCE;

        @Override
        public TranslationMode getMode() {
            return Default.mode;
        }

        @Override
        public String getSourceLanguage() {
            return Default.sourceLanguage;
        }

        @Override
        public String getTargetLanguage() {
            return Default.targetLanguage;
        }

        @Override
        public TranslationService getService() {
            return Default.service;
        }

        @Override
        public String getOpenaiApiKey() {
            return Default.openaiApiKey;
        }

        @Override
        public OpenAIClientProvider.Api getOpenaiBaseUrl() {
            return Default.openaiBaseUrl;
        }

        @Override
        public String getOpenaiCustomBaseUrl() {
            return Default.openaiCustomBaseUrl;
        }

        @Override
        public String getOpenaiModel() {
            return Default.openaiModel;
        }

        @Override
        public String getDeepLApiKey() {
            return Default.deeplApiKey;
        }

        @Override
        public String getLibreApiKey() {
            return Default.libreApiKey;
        }

        @Override
        public String getLibreBaseUrl() {
            return Default.libreBaseUrl;
        }

        @Override
        public String getOllamaBaseUrl() {
            return Default.ollamaBaseUrl;
        }

        @Override
        public String getOllamaModel() {
            return Default.ollamaModel;
        }

        public static void init() {
            ClientTickCallbacks.POST.register(client -> {
                while (TPPKeyMappings.CONFIG_KEY.consumeClick()) {
                    MessageUtl.toLocal(client, Component.translatable("misc.translatorpp.missing.clothconfig"));
                }
            });
        }
    }

    /**
     * To store default values.
     */
    interface Default {
        TranslationMode mode = TranslationMode.NAME_TOP;
        String sourceLanguage = "auto";
        String targetLanguage = "zh-CN";
        TranslationService service = TranslationService.GoogleTranslation;
        String openaiApiKey = "";
        OpenAIClientProvider.Api openaiBaseUrl = OpenAIClientProvider.Api.OpenAI;
        String openaiCustomBaseUrl = "https://custom.api.url/";
        String openaiModel = "";
        String deeplApiKey = "";
        String libreApiKey = "";
        String libreBaseUrl = "https://libretranslate.com/";
        String ollamaBaseUrl = "http://127.0.0.1:11434/";
        String ollamaModel = "";
    }
}