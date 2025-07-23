package net.psunset.translatorpp.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.neoforge.config.TPPConfigImplNeoForge;
import net.psunset.translatorpp.neoforge.event.TPPEventsNeoForge;

@Mod(value = TranslatorPP.ID, dist = Dist.CLIENT)
public final class TranslatorPPNeoForge {
    public TranslatorPPNeoForge(ModContainer container, IEventBus modBus, Dist dist) {
        var gameBus = NeoForge.EVENT_BUS;

        /* Earlier */

        TranslatorPP.init();
        TPPConfigImplNeoForge.init(container);

        /* Later */
        TPPEventsNeoForge.init(gameBus, modBus);
    }
}
