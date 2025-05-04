package net.psunset.translatorpp.neoforge.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.neoforge.client.gui.TPPConfigScreen;
import net.psunset.translatorpp.neoforge.translation.TranslationKit;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationTools;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = TranslatorPP.ID, bus = EventBusSubscriber.Bus.MOD)
public class TPPConfig {

    public static final General GENERAL;
    public static final ModConfigSpec generalSpec;

    public static final OpenAI OPENAI;
    public static final ModConfigSpec openaiSpec;

    static {
        final Pair<General, ModConfigSpec> generalPair = new ModConfigSpec.Builder().configure(General::new);
        GENERAL = generalPair.getLeft();
        generalSpec = generalPair.getRight();
        final Pair<OpenAI, ModConfigSpec> openaiPair = new ModConfigSpec.Builder().configure(OpenAI::new);
        OPENAI = openaiPair.getLeft();
        openaiSpec = openaiPair.getRight();

    }

    public static class General {

        public final ModConfigSpec.ConfigValue<String> sourceLanguage;
        public final ModConfigSpec.ConfigValue<String> targetLanguage;
        public final ModConfigSpec.EnumValue<TranslationTools> translationTool;
        public final ModConfigSpec.ConfigValue<String> openaiModel;

        private Set<String> openaiModels = Set.of();

        private General(ModConfigSpec.Builder builder) {
            Set<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::toLanguageTag)
                    .collect(Collectors.toSet());

            Set<String> slList = new HashSet<>(tlList.size() + 1);
            slList.add("auto");
            slList.addAll(tlList);

            this.sourceLanguage = builder
                    .translation("config.translatorpp.source_language")
                    .defineInList("source_language", "auto", slList);

            this.targetLanguage = builder
                    .translation("config.translatorpp.target_language")
                    .defineInList("target_language", "es-ES", tlList);

            this.translationTool = builder
                    .translation("config.translatorpp.translation_tool")
                    .defineEnum("translation_tool", TranslationTools.GoogleTranslation, EnumGetMethod.NAME_IGNORECASE);

            this.openaiModel = builder
                    .translation("config.translatorpp.openai_model")
                    .define("openai_model", OpenAIClientTool.BaseUrl.OpenAI.defaultModel, it ->
                            it.toString().isBlank() || !OpenAIClientTool.getInstance().isPresent() || (OpenAIClientTool.getInstance().isPresent() && this.openaiModels.contains(it)));
        }

        private void refreshOpenAIModels() {
            this.openaiModels = new HashSet<>(OpenAIClientTool.getInstance().getModels());
        }
    }

    public static class OpenAI {

        public final ModConfigSpec.ConfigValue<String> openaiApiKey;
        public final ModConfigSpec.ConfigValue<OpenAIClientTool.BaseUrl> openaiBaseUrl;

        private OpenAI(ModConfigSpec.Builder builder) {
            this.openaiApiKey = builder
                    .translation("config.translatorpp.openai_apikey")
                    .define("openai_apikey", "********************");

            this.openaiBaseUrl = builder
                    .translation("config.translatorpp.openai_baseurl")
                    .defineEnum("openai_baseurl", OpenAIClientTool.BaseUrl.OpenAI, EnumGetMethod.NAME_IGNORECASE);
        }
    }

    public static void commonInit(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, generalSpec, TranslatorPP.ID + "-general.toml");
        container.registerConfig(ModConfig.Type.CLIENT, openaiSpec, TranslatorPP.ID + "-openai.toml");
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, TPPConfigScreen::new);
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec().equals(generalSpec)) {

        } else if (event.getConfig().getSpec().equals(openaiSpec)) {
            TranslationKit.refreshOpenAIClientTool();
            GENERAL.refreshOpenAIModels();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec().equals(generalSpec)) {
            TranslationKit.refreshOpenAIClientTool();
            TranslationKit.getInstance().clearCache();
        } else if (event.getConfig().getSpec().equals(openaiSpec)) {
            TranslationKit.refreshOpenAIClientTool();
            GENERAL.refreshOpenAIModels();
        }
    }
}
