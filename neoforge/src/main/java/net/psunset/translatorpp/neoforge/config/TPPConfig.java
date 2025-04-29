package net.psunset.translatorpp.neoforge.config;

import com.ibm.icu.impl.locale.BaseLocale;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        List<String> availableLanguages = new ArrayList<>(Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toLanguageTag)
                .map(String::toLowerCase)
                .distinct()
                .sorted(String::compareTo)
                .toList());

        this.targetLanguage = builder
                .translation("config.translatorpp.target_language")
                .comment(I18n.get("config.translatorpp.target_language.comment")) // The language to translate to.
                .define("target_language", "en-US",
                        it -> availableLanguages.contains(it.toString().replace('_', '-').toLowerCase()));

        availableLanguages.addFirst("auto");

        this.sourceLanguage = builder
                .translation("config.translatorpp.source_language")
                .comment(I18n.get("config.translatorpp.source_language.comment")) // The language to translate from. Set to 'auto' to detect automatically.
                .define("source_language", "auto",
                        it -> availableLanguages.contains(it.toString().replace('_', '-').toLowerCase()));
    }

    public static void commonInit(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientInit(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
