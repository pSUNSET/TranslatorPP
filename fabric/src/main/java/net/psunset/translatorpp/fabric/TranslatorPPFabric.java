package net.psunset.translatorpp.fabric;

import net.psunset.translatorpp.TranslatorPP;
import net.fabricmc.api.ModInitializer;

public final class TranslatorPPFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TranslatorPP.commonInit();
    }
}
