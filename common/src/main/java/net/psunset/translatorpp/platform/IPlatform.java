package net.psunset.translatorpp.platform;

public interface IPlatform {
    boolean isForge();
    boolean isFabric();
    boolean isModLoaded(String modId);
}
