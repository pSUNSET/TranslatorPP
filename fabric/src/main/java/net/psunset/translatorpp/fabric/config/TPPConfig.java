package net.psunset.translatorpp.fabric.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.keybind.TPPKeyMappingsFabric;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Config(name = TranslatorPP.ID)
public class TPPConfig implements ConfigData {

    private static ConfigHolder<TPPConfig> HOLDER;

    public static TPPConfig getInstance() {
        return HOLDER.getConfig();
    }

    public static ConfigHolder<TPPConfig> getHolder() {
        return HOLDER;
    }

    public String sourceLanguage = "auto";
    public String targetLanguage = "es-ES";
    public String translationTool = "Google Translation";
    public String openaiModel = "gpt-4o-mini";

    public static void init() {
        HOLDER = AutoConfig.register(TPPConfig.class, GsonConfigSerializer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TPPKeyMappingsFabric.CONFIG_KEY.isDown()) {
                if (client.screen == null ||
                        !client.screen.getTitle().getString().equals(I18n.get("config.title.translatorpp"))) {
                    client.setScreen(TPPConfig.screen(client.screen));
                }
            }
        });
    }

    public static Screen screen(Screen parent) {
        TPPConfig config = TPPConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable(() -> TPPConfig.getHolder().save())
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

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.translation_tool"), config.translationTool)
                .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                .setSelections(TranslationTools.nameToTool.keySet())
                .setDefaultValue("Google Translation")
                .setSaveConsumer(it -> config.translationTool = it)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), config.openaiModel)
                .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                .setSelections(OpenAIClientTool.getInstance().getModels())
                .setDefaultValue(() -> OpenAIClientTool.BaseUrl.OpenAI.defaultModel)
                .setSaveConsumer(it -> config.openaiModel = it)
                .build());

        return builder.build();
    }
}
