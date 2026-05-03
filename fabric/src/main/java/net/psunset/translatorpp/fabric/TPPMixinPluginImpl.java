package net.psunset.translatorpp.fabric;

import net.psunset.translatorpp.TPPMixinPlugin;
import net.psunset.translatorpp.platform.fabric.PlatformImpl;

public class TPPMixinPluginImpl extends TPPMixinPlugin {
    static {
        PlatformImpl.init();
    }
}
