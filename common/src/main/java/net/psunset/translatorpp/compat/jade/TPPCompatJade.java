package net.psunset.translatorpp.compat.jade;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.psunset.translatorpp.core.TranslationKit;
import net.psunset.translatorpp.event.ClientTickCallbacks;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class TPPCompatJade implements IWailaPlugin {

    public static void init() {
        ClientTickCallbacks.POST.register(client -> {
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

        registration.registerBlockComponent(TPPJadeExtension.BLOCK, Block.class);
        registration.registerEntityComponent(TPPJadeExtension.ENTITY, Entity.class);
    }
}
