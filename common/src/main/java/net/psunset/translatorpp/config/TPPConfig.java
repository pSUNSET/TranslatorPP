package net.psunset.translatorpp.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.platform.Platform;
import net.psunset.translatorpp.tool.ClientUtl;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationMode;
import net.psunset.translatorpp.translation.TranslationTool;

public interface TPPConfig {
    TranslationMode getTranslationMode();

    String getSourceLanguage();

    String getTargetLanguage();

    TranslationTool.Type getTranslationTool();

    String getOpenaiModel();

    String getOpenaiApiKey();

    OpenAIClientTool.Api getOpenaiBaseUrl();

    String getOpenaiCustomBaseUrl();

    @Environment(EnvType.CLIENT)
    static void init() {
        if (Platform.isNeoForge()) {
            TranslatorPP.LOGGER.debug("NeoForge is loaded, using neoforge for Translator++ Config.");
            // Injected
        } else if (CompatUtl.ClothConfig.isLoaded()) {
            TranslatorPP.LOGGER.debug("Cloth Config is loaded, using cloth config for Translator++ Config.");
            Dummy.INSTANCE = new TPPConfigImplCloth();
            TPPConfigImplCloth.init();
        } else {
            TranslatorPP.LOGGER.debug("No config API is loaded, using default values for Translator++ Config.");
            Dummy.INSTANCE = new TPPConfig.Dummy();
            Dummy.init();
        }
    }

    static TPPConfig getInstance() {
        return Dummy.INSTANCE;
    }

    class Dummy implements TPPConfig {

        /**
         * The instance of the TPPConfig.
         * Not only works for the dummy one.
         */
        public static TPPConfig INSTANCE;

        @Override
        public TranslationMode getTranslationMode() {
            return Default.translationMode;
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
        public TranslationTool.Type getTranslationTool() {
            return Default.translationTool;
        }

        @Override
        public String getOpenaiModel() {
            return Default.openaiModel;
        }

        @Override
        public String getOpenaiApiKey() {
            return Default.openaiApiKey;
        }

        @Override
        public OpenAIClientTool.Api getOpenaiBaseUrl() {
            return Default.openaiBaseUrl;
        }

        @Override
        public String getOpenaiCustomBaseUrl() {
            return Default.openaiCustomBaseUrl;
        }

        public static void init() {
            ClientTickCallbacks.POST.register(client -> {
                while (TPPKeyMappings.CLOTH_CONFIG_KEY.consumeClick()) {
                     ClientUtl.message(client, Component.translatable("misc.translatorpp.missing.clothconfig"));
                }
            });
        }
    }

    interface Default {
        TranslationMode translationMode = TranslationMode.NAME_ONLY;
        String sourceLanguage = "auto";
        String targetLanguage = "zh-CN";
        TranslationTool.Type translationTool = TranslationTool.Type.GoogleTranslation;
        String openaiModel = "";
        String openaiApiKey = "";
        OpenAIClientTool.Api openaiBaseUrl = OpenAIClientTool.Api.OpenAI;
        String openaiCustomBaseUrl = "https://custom.api.url/";
    }
}
