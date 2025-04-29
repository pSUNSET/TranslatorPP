package net.psunset.translatorpp.fabric;

import net.psunset.translatorpp.TranslatorPP;
import net.fabricmc.api.ModInitializer;

public final class TranslatorPPFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        TranslatorPP.init();
    }
}
