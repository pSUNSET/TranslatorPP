package net.psunset.translatorpp.platform.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
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
