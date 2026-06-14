package net.psunset.translatorpp.keybind.fabric;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.psunset.translatorpp.keybind.TPPKeyMappings;

public final class TPPKeyMappingsImpl {

    public static void init() {
        for (var key : TPPKeyMappings.getEntries()) {
            KeyMappingHelper.registerKeyMapping(key);
        }
    }
}
