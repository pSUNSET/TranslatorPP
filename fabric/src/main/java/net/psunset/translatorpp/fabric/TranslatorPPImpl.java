package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.event.fabric.TPPEventsImpl;
import net.psunset.translatorpp.keybind.fabric.TPPKeyMappingsImpl;

public final class TranslatorPPImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        /* Earlier */

        TranslatorPP.init();
        TPPKeyMappingsImpl.init();

        /* Later */
        TPPEventsImpl.init();
    }
}
