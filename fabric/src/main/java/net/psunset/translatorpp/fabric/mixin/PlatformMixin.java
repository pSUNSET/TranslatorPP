package net.psunset.translatorpp.fabric.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.psunset.translatorpp.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Platform.class)
public class PlatformMixin {
    @Inject(method = "isNeoForge", at = @At("HEAD"), cancellable = true)
    private static void beforeIsNeoForge(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "isFabric", at = @At("HEAD"), cancellable = true)
    private static void beforeIsFabric(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "isModLoaded", at = @At("HEAD"), cancellable = true)
    private static void beforeIsModLoaded(String modId, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(FabricLoader.getInstance().isModLoaded(modId));
    }
}
