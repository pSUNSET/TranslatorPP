package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.config.TPPConfig;
import net.psunset.translatorpp.fabric.translation.TranslationKit;

public final class TranslatorPPFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TranslatorPP.commonInit();
        TranslationKit.commonInit();
    }
}
