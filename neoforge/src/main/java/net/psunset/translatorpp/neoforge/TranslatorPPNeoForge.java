package net.psunset.translatorpp.neoforge;

import net.psunset.translatorpp.TranslatorPP;
import net.neoforged.fml.common.Mod;

@Mod(TranslatorPP.MOD_ID)
public final class TranslatorPPNeoForge {
    public TranslatorPPNeoForge() {
        // Run our common setup.
        TranslatorPP.init();
    }
}
