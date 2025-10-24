package net.psunset.translatorpp;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.psunset.translatorpp.compat.jade.TPPCompatJade;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.translation.TranslationKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TranslatorPP {
    public static final String ID = "translatorpp";
    public static final String NAME = "Translator++";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    @Environment(EnvType.CLIENT)
    public static void init() {
        TranslationKit.init();
        TPPConfig.init();
        if (CompatUtl.Jade.isLoaded()) {
            TPPCompatJade.init();
        }
    }
}
