package net.psunset.translatorpp.neoforge.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.psunset.translatorpp.platform.IPlatform;
import net.psunset.translatorpp.platform.Platform;

public final class PlatformNeoForge implements IPlatform {

    static {
        Platform.INSTANCE = new PlatformNeoForge();
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
        return ModList.get().isLoaded(modId);
    }

    @OnlyIn(Dist.CLIENT)
    public static void init() {
    }
}
