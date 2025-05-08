package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.translation.TranslationKitEvents;

public final class TranslatorPPFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        TranslatorPP.commonInit();
        TranslationKitEvents.commonInit();
    }
}
