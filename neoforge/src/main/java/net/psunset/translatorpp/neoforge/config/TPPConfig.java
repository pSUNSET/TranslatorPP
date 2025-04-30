package net.psunset.translatorpp.neoforge.config;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class TPPConfig {
    public static final TPPConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<TPPConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(TPPConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<String> sourceLanguage;
    public final ModConfigSpec.ConfigValue<String> targetLanguage;

    private TPPConfig(ModConfigSpec.Builder builder) {
        Set<String> tlList = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag)
                .collect(Collectors.toSet());

        Set<String> slList = new HashSet<>(tlList.size() + 1);
        slList.add("auto");
        slList.addAll(tlList);

        this.sourceLanguage = builder
                .translation("config.translatorpp.source_language")
                .comment("The language to translate from. Set to 'auto' to detect automatically.")
                .defineInList("source_language", "auto", slList);

        this.targetLanguage = builder
                .translation("config.translatorpp.target_language")
                .comment("The language to translate to.")
                .defineInList("target_language", "es-ES", tlList);
    }

    public static void commonInit(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, SPEC);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
