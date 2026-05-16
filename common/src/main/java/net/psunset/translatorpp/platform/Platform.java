package net.psunset.translatorpp.platform;

import org.jetbrains.annotations.ApiStatus;

public final class Platform {

    @ApiStatus.Internal
    public static IPlatform _innerImpl;

    /**
     * Returns true if the current platform is Forge.
     */
    public static boolean isForge() {
        return _innerImpl.isForge();
    }

    /**
     * Returns true if the current platform is Fabric.
     */
    public static boolean isFabric() {
        return _innerImpl.isFabric();
    }

    /**
     * Returns true if the mod with the given ID is loaded.
     */
    public static boolean isModLoaded(String modId) {
        return _innerImpl.isModLoaded(modId);
    }
}
