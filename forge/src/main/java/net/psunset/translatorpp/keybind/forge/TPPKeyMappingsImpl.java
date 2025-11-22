package net.psunset.translatorpp.keybind.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.keybind.TPPKeyMappings;

@Mod.EventBusSubscriber(modid = TranslatorPP.ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TPPKeyMappingsImpl {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        for (var key : TPPKeyMappings.getEntries()) {
            event.register(key);
        }
    }
}
