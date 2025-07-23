package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.event.TPPEventsFabric;
import net.psunset.translatorpp.fabric.keybind.TPPKeyMappingsFabric;

public final class TranslatorPPFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        /* Earlier */

        TranslatorPP.init();
        TPPKeyMappingsFabric.init();

        /* Later */
        TPPEventsFabric.init();
    }
}
