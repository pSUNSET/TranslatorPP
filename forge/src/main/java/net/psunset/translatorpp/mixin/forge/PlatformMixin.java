package net.psunset.translatorpp.mixin.forge;

import net.minecraftforge.fml.ModList;
import net.psunset.translatorpp.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Platform.class, remap = false)
public class PlatformMixin {

    @Overwrite
    public static boolean isForge() {
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
