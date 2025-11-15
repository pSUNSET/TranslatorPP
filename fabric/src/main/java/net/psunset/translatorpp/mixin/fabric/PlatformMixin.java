package net.psunset.translatorpp.mixin.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.psunset.translatorpp.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Platform.class)
public class PlatformMixin {

    @Overwrite
    public static boolean isNeoForge() {
        return false;
    }

    @Overwrite
    public static boolean isFabric() {
        return true;
    }

    @Overwrite
    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
