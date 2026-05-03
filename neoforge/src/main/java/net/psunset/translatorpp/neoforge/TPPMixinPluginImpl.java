package net.psunset.translatorpp.neoforge;

import net.psunset.translatorpp.TPPMixinPlugin;
import net.psunset.translatorpp.platform.neoforge.PlatformImpl;

public class TPPMixinPluginImpl extends TPPMixinPlugin {
    static {
        PlatformImpl.init();
    }
}
