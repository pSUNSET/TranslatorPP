package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;

public final class TranslatorPPFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TranslatorPP.clientInit();
    }
}
