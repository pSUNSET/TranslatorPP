package net.psunset.translatorpp.platform;

import org.jetbrains.annotations.ApiStatus;

public final class Platform {

    /**
     * Directly using this field is discouraged. Use the static helper methods in this class instead.
     */
    @ApiStatus.Internal
    public static IPlatform INSTANCE;

    /**
     * Returns true if the current platform is NeoForge.
     */
    public static boolean isNeoForge() {
        return INSTANCE.isNeoForge();
    }

    /**
     * Returns true if the current platform is Fabric.
     */
    public static boolean isFabric() {
        return INSTANCE.isFabric();
    }

    /**
     * Returns true if the mod with the given ID is loaded.
     */
    public static boolean isModLoaded(String modId) {
        return INSTANCE.isModLoaded(modId);
    }
}
