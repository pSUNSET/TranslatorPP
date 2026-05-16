package net.psunset.translatorpp.platform.neoforge;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.psunset.translatorpp.platform.IPlatform;
import net.psunset.translatorpp.platform.Platform;

public final class PlatformImpl implements IPlatform {

    static {
        Platform._innerImpl = new PlatformImpl();
    }

    private PlatformImpl() {
    }

    @Override
    public boolean isNeoForge() {
        return true;
    }

    @Override
    public boolean isFabric() {
        return false;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get() == null ?
                FMLLoader.getLoadingModList().getMods().stream().anyMatch(modInfo -> modInfo.getModId().equals(modId)) :
                ModList.get().isLoaded(modId);
    }

    /**
     * Does nothing but simply run the code in static field
     */
    public static void init() {
    }
}
