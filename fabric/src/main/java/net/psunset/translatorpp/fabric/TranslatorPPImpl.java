package net.psunset.translatorpp.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.event.fabric.TPPEventsImpl;
import net.psunset.translatorpp.keybind.fabric.TPPKeyMappingsImpl;
import net.psunset.translatorpp.platform.fabric.PlatformImpl;

public final class TranslatorPPImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PlatformImpl.init();

        /* Earlier */

        TranslatorPP.init();
        TPPKeyMappingsImpl.init();

        /* Later */

        TPPEventsImpl.init();
    }
}
