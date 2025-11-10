package net.psunset.translatorpp.platform;

import org.jetbrains.annotations.ApiStatus;

public final class Platform {

    @ApiStatus.Internal
    public static IPlatform INSTANCE;

    public static boolean isNeoForge() {
        return INSTANCE.isNeoForge();
    }

    public static boolean isFabric() {
        return INSTANCE.isFabric();
    }

    public static boolean isModLoaded(String modId) {
        return INSTANCE.isModLoaded(modId);
    }
}
