package net.psunset.translatorpp.forge;

import net.psunset.translatorpp.TPPMixinPlugin;
import net.psunset.translatorpp.platform.forge.PlatformImpl;

public class TPPMixinPluginImpl extends TPPMixinPlugin {
    static {
        PlatformImpl.init();
    }
}
