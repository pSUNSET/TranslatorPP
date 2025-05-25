package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.translation.TranslationKitEvents;

public final class TranslatorPPFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TranslatorPP.init();
        TranslationKitEvents.init();
    }
}
