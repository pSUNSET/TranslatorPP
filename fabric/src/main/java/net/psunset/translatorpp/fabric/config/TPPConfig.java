package net.psunset.translatorpp.fabric.config;

import com.google.common.collect.ImmutableList;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.keybind.TPPKeyMappingsFabric;
import net.psunset.translatorpp.fabric.translation.TranslationKit;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Config(name = TranslatorPP.ID)
public class TPPConfig implements ConfigData {

    private static ConfigHolder<TPPConfig> HOLDER;
    private static ImmutableList<String> openaiModels = ImmutableList.of();

    public static TPPConfig getInstance() {
        return HOLDER.getConfig();
    }

    public static ConfigHolder<TPPConfig> getHolder() {
        return HOLDER;
    }

    public String sourceLanguage = "auto";
    public String targetLanguage = "es-ES";
    public TranslationTools translationTool = TranslationTools.GoogleTranslation;
    public String openaiModel = "gpt-4o-mini";
    public String openaiApiKey = "";
    public OpenAIClientTool.Api openaiBaseUrl = OpenAIClientTool.Api.OpenAI;

    @Environment(EnvType.CLIENT)
    public static void init() {
        HOLDER = AutoConfig.register(TPPConfig.class, GsonConfigSerializer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TPPKeyMappingsFabric.CONFIG_KEY.isDown()) {
                client.setScreen(TPPConfig.screen(client.screen));
            }
        });

        TranslationKit.refreshOpenAIClientTool();
        refreshOpenAIModels();
    }

    public static Screen screen(Screen parent) {
        TPPConfig config = TPPConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable(() -> {
                    TPPConfig.getHolder().save();
                    TranslationKit.getInstance().clearCache();
                    TranslationKit.refreshOpenAIClientTool();
                    refreshOpenAIModels();
                })
                .setTitle(Component.translatable("config.title.translatorpp"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.general"));
        ConfigCategory openaiCategory = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.openai"));

        List<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag)
                .distinct()
                .sorted(String::compareTo)
                .toList();

        List<String> slList = new ArrayList<>(tlList.size() + 1);
        slList.add("auto");
        slList.addAll(tlList);

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), config.sourceLanguage)
                .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                .setSelections(slList)
                .setDefaultValue("auto")
                .setSaveConsumer(it -> config.sourceLanguage = it)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), config.targetLanguage)
                .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                .setSelections(tlList)
                .setDefaultValue("es-ES")
                .setSaveConsumer(it -> config.targetLanguage = it)
                .build());

        generalCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_tool"), TranslationTools.class, config.translationTool)
                .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                .setDefaultValue(TranslationTools.GoogleTranslation)
                .setSaveConsumer(it -> config.translationTool = it)
                .build());

        generalCategory.addEntry(FocusedDropdownMenuBuilder.start(entryBuilder, Component.translatable("config.translatorpp.openai_model"), DropdownMenuBuilder.TopCellElementBuilder.of(config.openaiModel, it -> it, Component::literal), new DropdownBoxEntry.DefaultSelectionCellCreator<>())
                .setSelectionsSupplier(() -> openaiModels)
                .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                .setDefaultValue(OpenAIClientTool.Api.OpenAI.defaultModel)
                .setSaveConsumer(it -> config.openaiModel = it)
                .build());

        openaiCategory.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), config.openaiApiKey)
                .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                .setDefaultValue("")
                .setSaveConsumer(it -> config.openaiApiKey = it)
                .build());

        openaiCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientTool.Api.class, config.openaiBaseUrl)
                .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                .setDefaultValue(OpenAIClientTool.Api.OpenAI)
                .setSaveConsumer(it -> config.openaiBaseUrl = it)
                .build());

        return builder.build();
    }

    public static void refreshOpenAIModels() {
        openaiModels = ImmutableList.copyOf(OpenAIClientTool.getInstance().getModels());
    }
}