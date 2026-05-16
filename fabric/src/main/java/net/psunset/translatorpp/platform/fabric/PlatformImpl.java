package net.psunset.translatorpp.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.psunset.translatorpp.platform.IPlatform;
import net.psunset.translatorpp.platform.Platform;

public final class PlatformImpl implements IPlatform {

    static {
        Platform._innerImpl = new PlatformImpl();
    }

    private PlatformImpl() {
    }

    @Override
    public boolean isForge() {
        return false;
    }

    @Override
    public boolean isFabric() {
        return true;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    /**
     * Does nothing but simply run the code in static field
     */
    public static void init() {
    }
}
