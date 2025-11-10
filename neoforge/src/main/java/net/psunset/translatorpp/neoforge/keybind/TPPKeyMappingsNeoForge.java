package net.psunset.translatorpp.neoforge.keybind;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.keybind.TPPKeyMappings;

@EventBusSubscriber(modid = TranslatorPP.ID, value = Dist.CLIENT)
public final class TPPKeyMappingsNeoForge {

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        for (var key : TPPKeyMappings.getEntries()) {
            event.register(key);
        }
    }
}
