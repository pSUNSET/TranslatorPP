package net.psunset.translatorpp.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.psunset.translatorpp.TranslatorPP;
import net.neoforged.fml.common.Mod;
import net.psunset.translatorpp.neoforge.config.TPPConfig;
import net.psunset.translatorpp.neoforge.tool.TranslationKit;

@Mod(value = TranslatorPP.ID)
public final class TranslatorPPNeoForge {
    public TranslatorPPNeoForge(ModContainer container, IEventBus bus, Dist dist) {
        TranslatorPP.commonInit();
        TPPConfig.commonInit(container);
    }
}
