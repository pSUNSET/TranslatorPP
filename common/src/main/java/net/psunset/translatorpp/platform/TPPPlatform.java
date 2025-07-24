package net.psunset.translatorpp.platform;

public interface TPPPlatform {
    boolean isNeoForge();
    boolean isFabric();
    boolean isModLoaded(String modId);
}