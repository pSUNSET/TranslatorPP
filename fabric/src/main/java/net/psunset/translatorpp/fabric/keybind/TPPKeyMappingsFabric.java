package net.psunset.translatorpp.fabric.keybind;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.psunset.translatorpp.keybind.TPPKeyMappings;

public class TPPKeyMappingsFabric {

    @Environment(EnvType.CLIENT)
    public static void init() {
        for (var key : TPPKeyMappings.getEntries()) {
            KeyBindingHelper.registerKeyBinding(key);
        }
    }
}