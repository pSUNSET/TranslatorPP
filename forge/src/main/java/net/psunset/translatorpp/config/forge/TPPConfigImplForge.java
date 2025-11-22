package net.psunset.translatorpp.config.forge;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.compat.clothconfig.gui.forge.TPPConfigClothScreenForge;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.gui.ClothConfigMissingScreen;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.tool.ClientUtl;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationKit;
import net.psunset.translatorpp.translation.TranslationMode;
import net.psunset.translatorpp.translation.TranslationTool;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The NeoForge-sided implementation of {@link TPPConfig}.
 * To get config values, use {@link TPPConfig#getInstance()}.
 */
@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = TranslatorPP.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TPPConfigImplForge implements TPPConfig {

    public static final General GENERAL;
    public static final ForgeConfigSpec generalSpec;

    public static final OpenAI OPENAI;
    public static final ForgeConfigSpec openaiSpec;

    static {
        final Pair<General, ForgeConfigSpec> generalPair = new ForgeConfigSpec.Builder().configure(General::new);
        GENERAL = generalPair.getLeft();
        generalSpec = generalPair.getRight();
        final Pair<OpenAI, ForgeConfigSpec> openaiPair = new ForgeConfigSpec.Builder().configure(OpenAI::new);
        OPENAI = openaiPair.getLeft();
        openaiSpec = openaiPair.getRight();
    }

    public TPPConfigImplForge() {
    }

    @Override
    public TranslationMode getTranslationMode() {
        return GENERAL.translationMode.get();
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

    @Override
    public String getOpenaiCustomBaseUrl() {
        return OPENAI.openaiCustomBaseUrl.get();
    }

    public static class General {

        public final ForgeConfigSpec.EnumValue<TranslationMode> translationMode;
        public final ForgeConfigSpec.ConfigValue<String> sourceLanguage;
        public final ForgeConfigSpec.ConfigValue<String> targetLanguage;
        public final ForgeConfigSpec.EnumValue<TranslationTool.Type> translationTool;
        public final ForgeConfigSpec.ConfigValue<String> openaiModel;

        private General(ForgeConfigSpec.Builder builder) {
            Set<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::toLanguageTag)
                    .collect(Collectors.toSet());

            Set<String> slList = new HashSet<>(tlList.size() + 1);
            slList.add("auto");
            slList.addAll(tlList);

            this.translationMode = builder
                    .translation("config.translatorpp.translation_mode")
                    .defineEnum("translation_mode", Default.translationMode, EnumGetMethod.NAME_IGNORECASE);

            this.sourceLanguage = builder
                    .translation("config.translatorpp.source_language")
                    .defineInList("source_language", Default.sourceLanguage, slList);

            this.targetLanguage = builder
                    .translation("config.translatorpp.target_language")
                    .defineInList("target_language", Default.targetLanguage, tlList);

            this.translationTool = builder
                    .translation("config.translatorpp.translation_tool")
                    .defineEnum("translation_tool", Default.translationTool, EnumGetMethod.NAME_IGNORECASE);

            this.openaiModel = builder
                    .translation("config.translatorpp.openai_model")
                    .define("openai_model", Default.openaiModel, it ->
                            it == null || it.toString().isBlank() || !OpenAIClientTool.getInstance().isPresent() || (OpenAIClientTool.getInstance().isPresent() && OpenAIClientTool.getCacheModels().contains(it)));
        }
    }

    public static class OpenAI {

        public final ForgeConfigSpec.ConfigValue<String> openaiApiKey;
        public final ForgeConfigSpec.ConfigValue<OpenAIClientTool.Api> openaiBaseUrl;
        public final ForgeConfigSpec.ConfigValue<String> openaiCustomBaseUrl;

        private OpenAI(ForgeConfigSpec.Builder builder) {
            this.openaiApiKey = builder
                    .translation("config.translatorpp.openai_apikey")
                    .define("openai_apikey", Default.openaiApiKey);

            this.openaiBaseUrl = builder
                    .translation("config.translatorpp.openai_baseurl")
                    .defineEnum("openai_baseurl", Default.openaiBaseUrl, EnumGetMethod.NAME_IGNORECASE);

            this.openaiCustomBaseUrl = builder
                    .translation("config.translatorpp.openai_custom_baseurl")
                    .define("openai_custom_baseurl", Default.openaiCustomBaseUrl);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void init() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, generalSpec, TranslatorPP.ID + "-general.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, openaiSpec, TranslatorPP.ID + "-openai.toml");

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) ->
                        CompatUtl.ClothConfig.isLoaded() ? TPPConfigClothScreenForge.create(parent) : new ClothConfigMissingScreen(parent)));

        if (CompatUtl.ClothConfig.isLoaded()) {
            TPPConfigClothScreenForge.init(); // register the afterClientTickIfHasClothConfig event callback
        } else {
            MinecraftForge.EVENT_BUS.addListener(TPPConfigImplForge::afterClientTickIfNoClothConfig);
        }
    }

    public static void afterClientTickIfNoClothConfig(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (TPPKeyMappings.CLOTH_CONFIG_KEY.consumeClick()) {
                ClientUtl.message(Component.translatable("misc.translatorpp.missing.clothconfig"));
            }
        }
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        var spec = event.getConfig().getSpec();
//        if (spec.equals(generalSpec)) {
//        } else
        if (spec.equals(openaiSpec)) { // The final config registered in this mod
            OpenAIClientTool.getInstance().refresh();
            OpenAIClientTool.refreshCacheModels();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        var spec = event.getConfig().getSpec();
        if (spec.equals(generalSpec)) {
            OpenAIClientTool.getInstance().refresh();
            TranslationKit.getInstance().clearCache();
        } else if (spec.equals(openaiSpec)) {
            OpenAIClientTool.getInstance().refresh();
            OpenAIClientTool.refreshCacheModels();
        }
    }
}
