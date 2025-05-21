package net.psunset.translatorpp.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.neoforge.config.TPPConfigImplNeoForge;

@Mod(value = TranslatorPP.ID)
public final class TranslatorPPNeoForge {
    public TranslatorPPNeoForge(ModContainer container, IEventBus bus, Dist dist) {
        TranslatorPP.commonInit();
        TPPConfigImplNeoForge.commonInit(container);
    }
}
