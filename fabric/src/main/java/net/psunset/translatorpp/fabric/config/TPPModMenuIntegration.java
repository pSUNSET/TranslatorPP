package net.psunset.translatorpp.fabric.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.util.*;

public class TPPModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> {
            TPPConfig config = TPPConfig.getInstance();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setSavingRunnable(() -> TPPConfig.getHolder().save())
                    .setTitle(Component.translatable("config.title.translatorpp"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("config.category.translatorpp.general"));

            List<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                    .map(Locale::toLanguageTag)
                    .distinct()
                    .sorted(String::compareTo)
                    .toList();

            List<String> slList = new ArrayList<>(tlList.size() + 1);
            slList.add("auto");
            slList.addAll(tlList);

            generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.source_language"), config.sourceLanguage)
                    .setTooltip(Component.translatable("config.translatorpp.source_language.comment"))
                    .setSelections(slList)
                    .setDefaultValue("auto")
                    .setSaveConsumer(it -> config.sourceLanguage = it)
                    .build());

            generalCategory.addEntry(entryBuilder.startStringDropdownMenu(Component.translatable("config.translatorpp.target_language"), config.targetLanguage)
                    .setTooltip(Component.translatable("config.translatorpp.target_language.comment"))
                    .setSelections(tlList)
                    .setDefaultValue("es-ES")
                    .setSaveConsumer(it -> config.targetLanguage = it)
                    .build());

            return builder.build();
        };
    }
}
