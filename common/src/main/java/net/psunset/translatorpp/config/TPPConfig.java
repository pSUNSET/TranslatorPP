package net.psunset.translatorpp.config;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.tool.PlayerUtl;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationTool;

public interface TPPConfig {
    String getSourceLanguage();

    String getTargetLanguage();

    TranslationTool.Type getTranslationTool();

    String getOpenaiModel();

    String getOpenaiApiKey();

    OpenAIClientTool.Api getOpenaiBaseUrl();

    @Environment(EnvType.CLIENT)
    static void init() {
        if (Platform.isNeoForge()) {
            TranslatorPP.LOGGER.info("NeoForge is loaded, using neoforge for config.");
            // Injected
        } else if (CompatUtl.ClothConfig.isLoaded()) {
            TranslatorPP.LOGGER.info("Cloth Config is loaded, using cloth config for config.");
            Default.INSTANCE = new TPPConfigImplCloth();
            TPPConfigImplCloth.init();
        } else {
            TranslatorPP.LOGGER.info("No config api is loaded, using default one for config.");
            Default.INSTANCE = new TPPConfig.Default();
            Default.init();
        }
    }

    static TPPConfig getInstance() {
        return Default.INSTANCE;
    }

    class Default implements TPPConfig {

        public static TPPConfig INSTANCE;

        @Override
        public String getSourceLanguage() {
            return "auto";
        }

        @Override
        public String getTargetLanguage() {
            return "ja-JP";
        }

        @Override
        public TranslationTool.Type getTranslationTool() {
            return TranslationTool.Type.GoogleTranslation;
        }

        @Override
        public String getOpenaiModel() {
            return "";
        }

        @Override
        public String getOpenaiApiKey() {
            return "";
        }

        @Override
        public OpenAIClientTool.Api getOpenaiBaseUrl() {
            return null;
        }

        public static void init() {
            ClientTickEvent.CLIENT_POST.register(client -> {
                while (TPPKeyMappings.CLOTH_CONFIG_KEY.consumeClick()) {
                     PlayerUtl.clientMessage(client, Component.translatable("misc.translatorpp.missing.clothconfig"));
                }
            });
        }
    }
}
