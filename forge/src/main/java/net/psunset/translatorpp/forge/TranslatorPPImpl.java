package net.psunset.translatorpp.forge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.forge.TPPConfigImplForge;
import net.psunset.translatorpp.event.forge.TPPEventsImpl;

@Mod(value = TranslatorPP.ID)
public final class TranslatorPPImpl {
    public TranslatorPPImpl() {
        if (FMLLoader.getDist().isDedicatedServer()) return;

        var gameBus = MinecraftForge.EVENT_BUS;
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        /* Earlier */

        TranslatorPP.init();
        TPPConfigImplForge.init();

        /* Later */
        TPPEventsImpl.init(gameBus, modBus);
    }
}
