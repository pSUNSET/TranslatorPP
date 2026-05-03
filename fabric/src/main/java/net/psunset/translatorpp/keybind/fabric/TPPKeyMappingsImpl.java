package net.psunset.translatorpp.keybind.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.psunset.translatorpp.keybind.TPPKeyMappings;

public final class TPPKeyMappingsImpl {

    public static void init() {
        for (var key : TPPKeyMappings.getEntries()) {
            KeyBindingHelper.registerKeyBinding(key);
        }
    }
}
