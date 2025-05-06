package net.psunset.translatorpp.fabric.config;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.keybind.TPPKeyMappingsFabric;
import net.psunset.translatorpp.fabric.translation.TranslationKit;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationTools;

import java.util.*;

@Config(name = TranslatorPP.ID)
public class TPPConfig implements ConfigData {

    private static ConfigHolder<TPPConfig> HOLDER;
    private static final HashSet<String> openaiModels = Sets.newHashSet();

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

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.translation_tool"), config.translationTool == null ? "" : config.translationTool.getDisplayName())
                .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                .setSelections(TranslationTools.entries.keySet())
                .setDefaultValue(TranslationTools.GoogleTranslation.getDisplayName())
                .setSaveConsumer(it -> config.translationTool = TranslationTools.entries.get(it))
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), config.openaiModel)
                .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                .setSelections(openaiModels::iterator)
                .setDefaultValue(OpenAIClientTool.Api.OpenAI.defaultModel)
                .setSaveConsumer(it -> config.openaiModel = it)
                .build());

        openaiCategory.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), config.openaiApiKey)
                .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                .setDefaultValue("")
                .setSaveConsumer(it -> config.openaiApiKey = it)
                .build());

        openaiCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_baseurl"), config.openaiBaseUrl == null ? "" : config.openaiBaseUrl.name())
                .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                .setSelections(OpenAIClientTool.Api.entries.keySet())
                .setDefaultValue(OpenAIClientTool.Api.OpenAI.name())
                .setSaveConsumer(it -> config.openaiBaseUrl = OpenAIClientTool.Api.entries.get(it))
                .build());

        return builder.build();
    }

    public static void refreshOpenAIModels() {
        openaiModels.clear();
        openaiModels.addAll(OpenAIClientTool.getInstance().getModels());
    }
}
