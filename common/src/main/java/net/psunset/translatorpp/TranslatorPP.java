package net.psunset.translatorpp;

import net.psunset.translatorpp.compat.jade.TPPCompatJade;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.core.TranslationKit;
import net.psunset.translatorpp.tool.CompatUtl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TranslatorPP {
    public static final String ID = "translatorpp";
    public static final String NAME = "Translator++";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private TranslatorPP() {
        throw new AssertionError("TranslatorPP should not be instantiated");
    }

    public static void init() {
        TranslationKit.init();
        TPPConfig.init();
        if (CompatUtl.Jade.isLoaded()) {
            TPPCompatJade.init();
        }
    }
}
