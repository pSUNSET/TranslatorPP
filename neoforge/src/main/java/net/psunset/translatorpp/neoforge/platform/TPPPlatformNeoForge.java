package net.psunset.translatorpp.neoforge.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;
import net.psunset.translatorpp.platform.TPPPlatform;
import net.psunset.translatorpp.platform.Platform;

public class TPPPlatformNeoForge implements TPPPlatform {

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
        Platform.INSTANCE = new TPPPlatformNeoForge();
    }
}