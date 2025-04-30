package net.psunset.translatorpp.fabric;

import net.psunset.translatorpp.TranslatorPP;
import net.fabricmc.api.ModInitializer;
import net.psunset.translatorpp.fabric.config.TPPConfig;
import net.psunset.translatorpp.fabric.tool.TranslationKit;

public final class TranslatorPPFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TranslatorPP.commonInit();
        TPPConfig.init();
        TranslationKit.commonInit();
    }
}
