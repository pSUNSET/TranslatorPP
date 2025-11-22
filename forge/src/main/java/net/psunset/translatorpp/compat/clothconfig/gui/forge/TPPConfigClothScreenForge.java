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

    @OnlyIn(Dist.CLIENT)
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(TPPConfigClothScreenForge::afterClientTickIfHasClothConfig);
    }

    public static void afterClientTickIfHasClothConfig(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (TPPKeyMappings.CLOTH_CONFIG_KEY.isDown()) {
                Minecraft.getInstance().setScreen(create(Minecraft.getInstance().screen));
            }
        }
    }

    @ApiStatus.Internal
    public static TPPConfigClothScreen create(Screen parent) {
        return new TPPConfigClothScreen(parent, new ScreenProvider[]{General.INSTANCE, OpenAI.INSTANCE});
    }

    /**
     * An edition of {@link net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth.General}
     */
    private static class General implements ScreenProvider {

        private static final General INSTANCE = new General();

        private General() {
        }

        @Override
        public Screen createScreen(Screen parent) {

            var config = TPPConfigImplForge.GENERAL;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> {
                        TPPConfigImplForge.generalSpec.save();
                    })
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            // The translation of the component doesn't exist because it's completely unaccessible.
            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.default"));

            List<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::toLanguageTag)
                    .distinct()
                    .sorted(String::compareTo)
                    .toList();

            List<String> slList = new ArrayList<>(tlList.size() + 1);
            slList.add("auto");
            slList.addAll(tlList);

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_mode"), TranslationMode.class, config.translationMode.get())
                    .setTooltip(Component.translatable("config.translatorpp.translation_mode.tooltip"))
                    .setEnumNameProvider(e -> ((TranslationMode) e).toComponent())
                    .setDefaultValue(TPPConfig.Default.translationMode)
                    .setSaveConsumer(config.translationMode::set)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), config.sourceLanguage.get())
                    .setTooltip(Component.translatable("config.translatorpp.source_language.tooltip"))
                    .setSelections(slList)
                    .setDefaultValue(TPPConfig.Default.sourceLanguage)
                    .setSaveConsumer(config.sourceLanguage::set)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), config.targetLanguage.get())
                    .setTooltip(Component.translatable("config.translatorpp.target_language.tooltip"))
                    .setSelections(tlList)
                    .setDefaultValue(TPPConfig.Default.targetLanguage)
                    .setSaveConsumer(config.targetLanguage::set)
                    .build());

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.translation_tool"), TranslationTool.Type.class, config.translationTool.get())
                    .setTooltip(Component.translatable("config.translatorpp.translation_tool.tooltip"))
                    .setEnumNameProvider(e -> ((TranslationTool.Type) e).toComponent())
                    .setDefaultValue(TPPConfig.Default.translationTool)
                    .setSaveConsumer(config.translationTool::set)
                    .build());

            category.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.openai_model"), config.openaiModel.get())
                    .setSelections(OpenAIClientTool.getCacheModels())
                    .setTooltip(Component.translatable("config.translatorpp.openai_model.tooltip"))
                    .setDefaultValue(TPPConfig.Default.openaiModel)
                    .setSaveConsumer(config.openaiModel::set)
                    .build());

            return builder.build();
        }
    }

    /**
     * An edition of {@link net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth.OpenAI}
     */
    private static class OpenAI implements ScreenProvider {

        private static final OpenAI INSTANCE = new OpenAI();

        private OpenAI() {
        }

        @Override
        public Screen createScreen(Screen parent) {

            var config = TPPConfigImplForge.OPENAI;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> {
                        TPPConfigImplForge.openaiSpec.save();
                    })
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            // The translation of the component doesn't exist because it's completely unaccessible.
            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.default"));

            category.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_apikey"), config.openaiApiKey.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_apikey.tooltip"))
                    .setDefaultValue(TPPConfig.Default.openaiApiKey)
                    .setSaveConsumer(config.openaiApiKey::set)
                    .build());

            category.addEntry(entryBuilder.startEnumSelector(Component.translatable("config.translatorpp.openai_baseurl"), OpenAIClientTool.Api.class, config.openaiBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_baseurl.tooltip"))
                    .setEnumNameProvider(e -> ((OpenAIClientTool.Api) e).toComponent())
                    .setDefaultValue(TPPConfig.Default.openaiBaseUrl)
                    .setSaveConsumer(config.openaiBaseUrl::set)
                    .build());

            category.addEntry(entryBuilder.startStrField(Component.translatable("config.translatorpp.openai_custom_baseurl"), config.openaiCustomBaseUrl.get())
                    .setTooltip(Component.translatable("config.translatorpp.openai_custom_baseurl.tooltip"))
                    .setDefaultValue(TPPConfig.Default.openaiCustomBaseUrl)
                    .setSaveConsumer(config.openaiCustomBaseUrl::set)
                    .build());

            return builder.build();
        }
    }
}
