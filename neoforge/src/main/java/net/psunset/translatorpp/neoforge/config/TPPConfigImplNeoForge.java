package net.psunset.translatorpp.neoforge.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.neoforge.compat.clothconfig.gui.TPPConfigClothScreenNeoForge;
import net.psunset.translatorpp.neoforge.config.gui.TPPConfigNeoForgeScreen;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.tool.ClientUtl;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationKit;
import net.psunset.translatorpp.translation.TranslationTool;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = TranslatorPP.ID, bus = EventBusSubscriber.Bus.MOD)
public class TPPConfigImplNeoForge implements TPPConfig {

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

    @Override
    public String getSourceLanguage() {
        return GENERAL.sourceLanguage.get();
    }

    @Override
    public String getTargetLanguage() {
        return GENERAL.targetLanguage.get();
    }

    @Override
    public TranslationTool.Type getTranslationTool() {
        return GENERAL.translationTool.get();
    }

    @Override
    public String getOpenaiModel() {
        return GENERAL.openaiModel.get();
    }

    @Override
    public String getOpenaiApiKey() {
        return OPENAI.openaiApiKey.get();
    }

    @Override
    public OpenAIClientTool.Api getOpenaiBaseUrl() {
        return OPENAI.openaiBaseUrl.get();
    }

    public static class General {

        public final ModConfigSpec.ConfigValue<String> sourceLanguage;
        public final ModConfigSpec.ConfigValue<String> targetLanguage;
        public final ModConfigSpec.EnumValue<TranslationTool.Type> translationTool;
        public final ModConfigSpec.ConfigValue<String> openaiModel;

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
                    .defineInList("target_language", "ja-JP", tlList);

            this.translationTool = builder
                    .translation("config.translatorpp.translation_tool")
                    .defineEnum("translation_tool", TranslationTool.Type.GoogleTranslation, EnumGetMethod.NAME_IGNORECASE);

            this.openaiModel = builder
                    .translation("config.translatorpp.openai_model")
                    .define("openai_model", "", it ->
                            it == null || it.toString().isBlank() || !OpenAIClientTool.getInstance().isPresent() || (OpenAIClientTool.getInstance().isPresent() && OpenAIClientTool.getCacheModels().contains(it)));
        }
    }

    public static class OpenAI {

        public final ModConfigSpec.ConfigValue<String> openaiApiKey;
        public final ModConfigSpec.ConfigValue<OpenAIClientTool.Api> openaiBaseUrl;

        private OpenAI(ModConfigSpec.Builder builder) {
            this.openaiApiKey = builder
                    .translation("config.translatorpp.openai_apikey")
                    .define("openai_apikey", "");

            this.openaiBaseUrl = builder
                    .translation("config.translatorpp.openai_baseurl")
                    .defineEnum("openai_baseurl", OpenAIClientTool.Api.OpenAI, EnumGetMethod.NAME_IGNORECASE);
        }
    }

    public static void commonInit(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, generalSpec, TranslatorPP.ID + "-general.toml");
        container.registerConfig(ModConfig.Type.CLIENT, openaiSpec, TranslatorPP.ID + "-openai.toml");
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, TPPConfigNeoForgeScreen::new);
        if (CompatUtl.ClothConfig.isLoaded()) {
            TPPConfigClothScreenNeoForge.init();
        } else {
            NeoForge.EVENT_BUS.addListener(TPPConfigImplNeoForge::afterClientTickIfNoClothConfig);
        }
    }

    public static void afterClientTickIfNoClothConfig(ClientTickEvent.Post event) {
        while (TPPKeyMappings.CLOTH_CONFIG_KEY.consumeClick()) {
            ClientUtl.message(Component.translatable("misc.translatorpp.missing.clothconfig"));
        }
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec().equals(generalSpec)) {
        } else if (event.getConfig().getSpec().equals(openaiSpec)) { // The final config registered in this mod
            TranslationKit.getInstance().refreshOpenAIClientTool();
            OpenAIClientTool.refreshCacheModels();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec().equals(generalSpec)) {
            TranslationKit.getInstance().refreshOpenAIClientTool();
            TranslationKit.getInstance().clearCache();
        } else if (event.getConfig().getSpec().equals(openaiSpec)) {
            TranslationKit.getInstance().refreshOpenAIClientTool();
            OpenAIClientTool.refreshCacheModels();
        }
    }
}
