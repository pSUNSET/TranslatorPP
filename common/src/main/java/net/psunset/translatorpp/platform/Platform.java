package net.psunset.translatorpp.platform;

public class Platform {

    public static TPPPlatform INSTANCE;

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
