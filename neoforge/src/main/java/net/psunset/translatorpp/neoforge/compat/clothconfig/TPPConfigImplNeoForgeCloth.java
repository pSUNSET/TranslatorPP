package net.psunset.translatorpp.neoforge.compat.clothconfig;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.compat.CompatUtl;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.neoforge.config.TPPConfigImplNeoForge;
import net.psunset.translatorpp.translation.OpenAIClientTool;
import net.psunset.translatorpp.translation.TranslationKit;
import net.psunset.translatorpp.translation.TranslationTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TPPConfigImplNeoForgeCloth {

    @OnlyIn(Dist.CLIENT)
    public static void init() {
        NeoForge.EVENT_BUS.addListener(TPPConfigImplNeoForgeCloth::afterClientTick);
    }

    public static void afterClientTick(ClientTickEvent.Post event) {
        if (TPPKeyMappings.CLOTH_CONFIG_KEY.isDown()) {
            Minecraft.getInstance().setScreen(screen(Minecraft.getInstance().screen));
        }
    }

    public static Screen screen(Screen parent) {

        TPPConfigImplNeoForge.General general = TPPConfigImplNeoForge.GENERAL;
        TPPConfigImplNeoForge.OpenAI openai = TPPConfigImplNeoForge.OPENAI;

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable(() -> {
                    TPPConfigImplNeoForge.generalSpec.save();
                    TPPConfigImplNeoForge.openaiSpec.save();
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

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), general.sourceLanguage.get())
                .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                .setSelections(slList)
                .setDefaultValue("auto")
                .setSaveConsumer(general.sourceLanguage::set)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), general.targetLanguage.get())
                .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                .setSelections(tlList)
                .setDefaultValue("es-ES")
                .setSaveConsumer(general.targetLanguage::set)
                .build());

        generalCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_tool"), TranslationTool.Type.class, general.translationTool.get())
                .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                .setDefaultValue(TranslationTool.Type.GoogleTranslation)
                .setSaveConsumer(general.translationTool::set)
                .build());

        generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), general.openaiModel.get())
                .setSelections(OpenAIClientTool.getInstance().getModelOffline())
                .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                .setDefaultValue(OpenAIClientTool.Api.OpenAI.defaultModel)
                .setSaveConsumer(general.openaiModel::set)
                .build());

        openaiCategory.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), openai.openaiApiKey.get())
                .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                .setDefaultValue("")
                .setSaveConsumer(openai.openaiApiKey::set)
                .build());

        openaiCategory.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientTool.Api.class, openai.openaiBaseUrl.get())
                .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                .setDefaultValue(OpenAIClientTool.Api.OpenAI)
                .setSaveConsumer(openai.openaiBaseUrl::set)
                .build());

        return builder.build();
    }
}
