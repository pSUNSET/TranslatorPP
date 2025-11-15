package net.psunset.translatorpp.mixin.neoforge;

import net.neoforged.fml.ModList;
import net.psunset.translatorpp.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Platform.class)
public class PlatformMixin {

    @Overwrite
    public static boolean isNeoForge() {
        return true;
    }

    @Overwrite
    public static boolean isFabric() {
        return false;
    }

    @Overwrite
    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
