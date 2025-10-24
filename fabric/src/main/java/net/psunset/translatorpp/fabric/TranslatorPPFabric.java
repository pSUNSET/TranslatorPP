package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.event.TPPEventsFabric;
import net.psunset.translatorpp.fabric.keybind.TPPKeyMappingsFabric;
import net.psunset.translatorpp.fabric.platform.PlatformFabric;

public final class TranslatorPPFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PlatformFabric.init();

        /* Earlier */

        TranslatorPP.init();
        TPPKeyMappingsFabric.init();

        /* Later */
        TPPEventsFabric.init();
    }
}
