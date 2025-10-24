package net.psunset.translatorpp.platform;

public interface IPlatform {
    boolean isNeoForge();
    boolean isFabric();
    boolean isModLoaded(String modId);
}