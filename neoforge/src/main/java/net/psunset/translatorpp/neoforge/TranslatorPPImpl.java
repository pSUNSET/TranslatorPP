package net.psunset.translatorpp.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.neoforge.TPPConfigImplNeoForge;
import net.psunset.translatorpp.event.neoforge.TPPEventsImpl;
import net.psunset.translatorpp.platform.neoforge.PlatformImpl;

@Mod(value = TranslatorPP.ID, dist = Dist.CLIENT)
public final class TranslatorPPImpl {

    public TranslatorPPImpl(ModContainer container, IEventBus modBus, Dist dist) {
        IEventBus gameBus = NeoForge.EVENT_BUS;

        PlatformImpl.init();

        /* Earlier */

        TranslatorPP.init();
        TPPConfigImplNeoForge.init(container);

        /* Later */

        TPPEventsImpl.init(gameBus, modBus);
    }
}
