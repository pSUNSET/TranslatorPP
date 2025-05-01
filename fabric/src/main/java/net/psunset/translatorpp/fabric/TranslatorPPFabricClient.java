package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.keybind.TPPKeyMappingsFabric;
import net.psunset.translatorpp.fabric.translation.TranslationKit;

public final class TranslatorPPFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TranslatorPP.clientInit();
        TranslationKit.clientInit();
        TPPKeyMappingsFabric.init();
    }
}
