package net.psunset.translatorpp.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.neoforge.config.TPPConfig;

@Mod(value = TranslatorPP.ID, dist = Dist.CLIENT)
public final class TranslatorPPNeoForgeClient {
    public TranslatorPPNeoForgeClient(ModContainer container, IEventBus modBus, Dist dist) {
        TranslatorPP.clientInit();
        TPPConfig.clientInit(container);
    }
}
