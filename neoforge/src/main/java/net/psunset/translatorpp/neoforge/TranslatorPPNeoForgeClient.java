package net.psunset.translatorpp.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.neoforge.config.TPPConfigImplNeoForge;
import net.psunset.translatorpp.neoforge.translation.TranslationKitEvents;

@Mod(value = TranslatorPP.ID, dist = Dist.CLIENT)
public final class TranslatorPPNeoForgeClient {
    public TranslatorPPNeoForgeClient(ModContainer container, IEventBus bus, Dist dist) {
        TranslatorPP.clientInit();
        TPPConfigImplNeoForge.clientInit(container);
    }
}
