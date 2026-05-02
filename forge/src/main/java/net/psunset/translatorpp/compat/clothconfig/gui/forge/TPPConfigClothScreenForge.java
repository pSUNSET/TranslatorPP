package net.psunset.translatorpp.compat.clothconfig.gui.forge;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.psunset.translatorpp.api.ScreenProvider;
import net.psunset.translatorpp.compat.clothconfig.gui.TPPConfigClothScreen;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.config.forge.TPPConfigImplForge;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationMode;
import net.psunset.translatorpp.translation.TranslationTool;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TPPConfigClothScreenForge {

    public static void initIfHasClothConfig(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (c, p) -> create(p));
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, TPPConfigClothScreenNeoForge::afterClientTickIfHasClothConfig);
    }

    public static void afterClientTickIfHasClothConfig(ClientTickEvent.Post event) {
        if (TPPKeyMappings.CONFIG_KEY.isDown()) {
            Minecraft.getInstance().setScreen(create(Minecraft.getInstance().screen));
        }
    }

    private static Screen create(Screen parent) {
        return Provider.INSTANCE.createScreen(parent);
    }

    private static final class Provider implements IScreenProvider {

        private static final Provider INSTANCE = new Provider();

        private General() {
        }

        @Override
        public Screen createScreen(Screen parent) {

            var config = TPPConfigImplNeoForge.INSTANCE;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(TPPConfigImplNeoForge.SPEC::save)
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

            general.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.mode"), TranslationMode.class, config.mode.get())
                    .setTooltip(Component.translatable("config.translatorpp.mode.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(TPPConfig.Default.mode)
                    .setSaveConsumer(config.mode::set)
                    .build());

            general.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), config.sourceLanguage.get())
                    .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                    .setSelections(slList)
                    .setDefaultValue(TPPConfig.Default.sourceLanguage)
                    .setSaveConsumer(config.sourceLanguage::set)
                    .build());

            general.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), config.targetLanguage.get())
                    .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                    .setSelections(tlList)
                    .setDefaultValue(TPPConfig.Default.targetLanguage)
                    .setSaveConsumer(config.targetLanguage::set)
                    .build());

            general.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.service"), TranslationService.class, config.service.get())
                    .setTooltip(Component.translatable("config.translatorpp.service.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(TPPConfig.Default.service)
                    .setSaveConsumer(config.service::set)
                    .build());

            /* ---------------------------------------- */

            openai.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), config.openaiApiKey.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                    .setDefaultValue(TPPConfig.Default.openaiApiKey)
                    .setSaveConsumer(config.openaiApiKey::set)
                    .build());

            openai.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientProvider.Api.class, config.openaiBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                    .setEnumNameProvider(e -> ((ComponentizableEnum) e).toComponent())
                    .setDefaultValue(TPPConfig.Default.openaiBaseUrl)
                    .setSaveConsumer(config.openaiBaseUrl::set)
                    .build());

            openai.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_custom_baseurl"), config.openaiCustomBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_custom_baseurl.tooltip"))
                    .setDefaultValue(TPPConfig.Default.openaiCustomBaseUrl)
                    .setSaveConsumer(config.openaiCustomBaseUrl::set)
                    .build());

            openai.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), config.openaiModel.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                    .setSelections(OpenAIClientProvider.getCacheModels())
                    .setDefaultValue(TPPConfig.Default.openaiModel)
                    .setSaveConsumer(config.openaiModel::set)
                    .build());

            /* ---------------------------------------- */

            deepl.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.deepl_apikey"), config.deeplApiKey.get())
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.tooltip"))
                    .setDefaultValue(TPPConfig.Default.deeplApiKey)
                    .setSaveConsumer(config.deeplApiKey::set)
                    .build());

            /* ---------------------------------------- */

            libre.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.libre_apikey"), config.libreApiKey.get())
                    .setTooltip(Component.translatable("config.translatorpp.libre_apikey.tooltip"))
                    .setDefaultValue(TPPConfig.Default.libreApiKey)
                    .setSaveConsumer(config.libreApiKey::set)
                    .build());

            libre.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.libre_baseurl"), config.libreBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.libre_baseurl"))
                    .setDefaultValue(TPPConfig.Default.libreBaseUrl)
                    .setSaveConsumer(config.libreBaseUrl::set)
                    .build());

            /* ---------------------------------------- */

            ollama.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.ollama_baseurl"), config.ollamaBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.deepl_apikey.ollama_baseurl"))
                    .setDefaultValue(TPPConfig.Default.ollamaBaseUrl)
                    .setSaveConsumer(config.ollamaBaseUrl::set)
                    .build());

            ollama.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.ollama_model"), config.ollamaModel.get())
                    .setTooltip(Component.translatable("config.translatorpp.ollama_model.tooltip"))
                    .setSelections(OllamaClientProvider.getCacheModels())
                    .setDefaultValue(TPPConfig.Default.ollamaModel)
                    .setSaveConsumer(config.ollamaModel::set)
                    .build());

            return builder.build();
        }
    }
}
