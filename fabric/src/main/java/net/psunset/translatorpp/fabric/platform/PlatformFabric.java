package net.psunset.translatorpp.fabric.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.psunset.translatorpp.platform.IPlatform;
import net.psunset.translatorpp.platform.Platform;

public class PlatformFabric implements IPlatform {
    @Override
    public boolean isNeoForge() {
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

    @Environment(EnvType.CLIENT)
    public static void init() {
        Platform.INSTANCE = new PlatformFabric();
    }
}