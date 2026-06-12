package net.psunset.translatorpp.config.forge;

import com.electronwill.nightconfig.core.EnumGetMethod;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.compat.clothconfig.gui.forge.TPPConfigClothScreenForge;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.core.*;
import net.psunset.translatorpp.gui.ClothConfigMissingScreen;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.tool.ClientUtl;
import net.psunset.translatorpp.tool.CompatUtl;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The NeoForge-sided implementation of {@link TPPConfig}.
 * To get config values, use {@link TPPConfig#getInstance()}.
 */
@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = TranslatorPP.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TPPConfigImplForge implements TPPConfig {

    public static final TPPConfigImplForge INSTANCE;
    public static final ForgeConfigSpec SPEC;
    public static final Map<String, String> FIRST_CHILD_TO_CATEGORY = new HashMap<>();

    static {
        final Pair<TPPConfigImplForge, ForgeConfigSpec> configPair = new ForgeConfigSpec.Builder().configure(TPPConfigImplForge::new);
        INSTANCE = configPair.getLeft();
        SPEC = configPair.getRight();
    }

    /* General */
    public final ForgeConfigSpec.EnumValue<TranslationMode> mode;
    public final ForgeConfigSpec.ConfigValue<String> sourceLanguage;
    public final ForgeConfigSpec.ConfigValue<String> targetLanguage;
    public final ForgeConfigSpec.EnumValue<TranslationService> service;

    /* OpenAI */
    public final ForgeConfigSpec.ConfigValue<String> openaiApiKey;
    public final ForgeConfigSpec.EnumValue<OpenAIClientProvider.Api> openaiBaseUrl;
    public final ForgeConfigSpec.ConfigValue<String> openaiCustomBaseUrl;
    public final ForgeConfigSpec.ConfigValue<String> openaiModel;

    /* DeepL */
    public final ForgeConfigSpec.ConfigValue<String> deeplApiKey;

    /* Libre */
    public final ForgeConfigSpec.ConfigValue<String> libreApiKey;
    public final ForgeConfigSpec.ConfigValue<String> libreBaseUrl;

    /* Ollama */
    public final ForgeConfigSpec.ConfigValue<String> ollamaBaseUrl;
    public final ForgeConfigSpec.ConfigValue<String> ollamaModel;

    private TPPConfigImplForge(ForgeConfigSpec.Builder builder) {
        Set<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag)
                .collect(Collectors.toSet());

        Set<String> slList = new HashSet<>(tlList.size() + 1);
        slList.add("auto");
        slList.addAll(tlList);

        /* ---------------------------------------- */
        FIRST_CHILD_TO_CATEGORY.put("mode", "general");

        this.mode = builder
                .translation("config.translatorpp.mode")
                .defineEnum("mode", Default.mode, EnumGetMethod.NAME_IGNORECASE);

        this.sourceLanguage = builder
                .translation("config.translatorpp.source_language")
                .defineInList("source_language", Default.sourceLanguage, slList);

        this.targetLanguage = builder
                .translation("config.translatorpp.target_language")
                .defineInList("target_language", Default.targetLanguage, tlList);

        this.service = builder
                .translation("config.translatorpp.service")
                .defineEnum("service", Default.service, EnumGetMethod.NAME_IGNORECASE);

        /* ---------------------------------------- */
        FIRST_CHILD_TO_CATEGORY.put("openai_apikey", "openai");

        this.openaiApiKey = builder
                .translation("config.translatorpp.openai_apikey")
                .define("openai_apikey", Default.openaiApiKey);

        this.openaiBaseUrl = builder
                .translation("config.translatorpp.openai_baseurl")
                .defineEnum("openai_baseurl", Default.openaiBaseUrl, EnumGetMethod.NAME_IGNORECASE);

        this.openaiCustomBaseUrl = builder
                .translation("config.translatorpp.openai_custom_baseurl")
                .define("openai_custom_baseurl", Default.openaiCustomBaseUrl);

        this.openaiModel = builder
                .translation("config.translatorpp.openai_model")
                .define("openai_model", Default.openaiModel, it ->
                        it == null ||  // DO NOT DELETE THIS LINE
                                it.toString().isEmpty() ||
                                !OpenAIClientProvider.getInstance().isPresent() ||
                                (OpenAIClientProvider.getInstance().isPresent() && OpenAIClientProvider.getCacheModels().contains(it.toString())));

        /* ---------------------------------------- */
        FIRST_CHILD_TO_CATEGORY.put("deepl_apikey", "deepl");

        this.deeplApiKey = builder
                .translation("config.translatorpp.deepl_apikey")
                .define("deepl_apikey", Default.deeplApiKey);

        /* ---------------------------------------- */
        FIRST_CHILD_TO_CATEGORY.put("libre_apikey", "libre");

        this.libreApiKey = builder
                .translation("config.translatorpp.libre_apikey")
                .define("libre_apikey", Default.libreApiKey);

        this.libreBaseUrl = builder
                .translation("config.translatorpp.libre_baseurl")
                .define("libre_baseurl", Default.libreBaseUrl);

        /* ---------------------------------------- */
        FIRST_CHILD_TO_CATEGORY.put("ollama_baseurl", "ollama");

        this.ollamaBaseUrl = builder
                .translation("config.translatorpp.ollama_baseurl")
                .define("ollama_baseurl", Default.ollamaBaseUrl);

        this.ollamaModel = builder
                .translation("config.translatorpp.ollama_model")
                .define("ollama_model", Default.ollamaModel, it ->
                        it == null ||  // DO NOT DELETE THIS LINE
                                it.toString().isEmpty() ||
                                !OllamaClientProvider.getInstance().isPresent() ||
                                (OllamaClientProvider.getInstance().isPresent() && OllamaClientProvider.getCacheModels().contains(it.toString())));
    }

    @Override
    public TranslationMode getMode() {
        return this.mode.get();
    }

    @Override
    public String getSourceLanguage() {
        return this.sourceLanguage.get();
    }

    @Override
    public String getTargetLanguage() {
        return this.targetLanguage.get();
    }

    @Override
    public TranslationService getService() {
        return this.service.get();
    }

    @Override
    public String getOpenaiApiKey() {
        return this.openaiApiKey.get();
    }

    @Override
    public OpenAIClientProvider.Api getOpenaiBaseUrl() {
        return this.openaiBaseUrl.get();
    }

    @Override
    public String getOpenaiCustomBaseUrl() {
        return this.openaiCustomBaseUrl.get();
    }

    @Override
    public String getOpenaiModel() {
        return this.openaiModel.get();
    }

    @Override
    public String getDeepLApiKey() {
        return this.deeplApiKey.get();
    }

    @Override
    public String getLibreApiKey() {
        return this.libreApiKey.get();
    }

    @Override
    public String getLibreBaseUrl() {
        return this.libreBaseUrl.get();
    }

    @Override
    public String getOllamaBaseUrl() {
        return this.ollamaBaseUrl.get();
    }

    @Override
    public String getOllamaModel() {
        return this.ollamaModel.get();
    }

    public static void init() {
        moveOldConfigToNew();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SPEC, TranslatorPP.ID + ".toml");

        if (CompatUtl.ClothConfig.isLoaded()) {
            TPPConfigClothScreenForge.initIfHasClothConfig();
        } else {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(ClothConfigMissingScreen::new));
            MinecraftForge.EVENT_BUS.<TickEvent.ClientTickEvent>addListener(TPPConfigImplForge::afterClientTickIfNoClothConfig);
        }
    }

    public static void afterClientTickIfNoClothConfig(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (TPPKeyMappings.CONFIG_KEY.consumeClick()) {
                ClientUtl.message(Component.translatable("misc.translatorpp.missing.clothconfig"));
            }
        }
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec().equals(SPEC)) {
            OpenAIClientProvider.getInstance().refresh();
            OpenAIClientProvider.refreshCacheModels();
            DeepLTranslationProvider.getInstance().refresh();
            LibreTranslateProvider.getInstance().refresh();
            OllamaClientProvider.getInstance().refresh();
            OllamaClientProvider.refreshCacheModels();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        // TODO: Refresh exact items by edited config
        if (event.getConfig().getSpec().equals(SPEC)) {
            TranslationKit.getInstance().clearCache();
            OpenAIClientProvider.getInstance().refresh();
            OpenAIClientProvider.refreshCacheModels();
            DeepLTranslationProvider.getInstance().refresh();
            LibreTranslateProvider.getInstance().refresh();
            OllamaClientProvider.getInstance().refresh();
            OllamaClientProvider.refreshCacheModels();
        }
    }

    private static void moveOldConfigToNew() {
        var configDir = FMLPaths.CONFIGDIR.get();
        var oConfigFile = configDir.resolve("translatorpp-client.toml");
        var configFile = configDir.resolve("translatorpp.toml");
        try {
            if (Files.exists(oConfigFile)) {
                if (Files.notExists(configFile)) {
                    byte[] oContent = Files.readAllBytes(oConfigFile);
                    Files.write(configFile, oContent, StandardOpenOption.CREATE);
                }
                Files.delete(oConfigFile);
            }
        } catch (IOException ignored) {
        }
    }
}
