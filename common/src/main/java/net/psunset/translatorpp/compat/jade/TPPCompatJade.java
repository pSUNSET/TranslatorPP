package net.psunset.translatorpp.compat.jade;

import dev.architectury.event.events.client.ClientTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import net.psunset.translatorpp.tool.CompatUtl;
import net.psunset.translatorpp.translation.TranslationKit;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class TPPCompatJade implements IWailaPlugin {

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (Minecraft.getInstance().screen == null) {
                if (TPPKeyMappings.TRANSLATE_KEY.isDown()) {
                    TranslationKit.getInstance().start(client);
                } else {
                    TranslationKit.getInstance().stop();
                }
            }
        });
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        IWailaPlugin.super.registerClient(registration);
        TPPCompatJade_18.registerClient(registration);
    }
}
