package net.psunset.translatorpp.neoforge.mixin;

import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.neoforge.config.TPPConfigImplNeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TPPConfig.class)
public interface TPPConfigMixin {

    @Inject(method = "init()V", at = @At(value = "INVOKE", target = "Ldev/architectury/platform/Platform;isNeoForge()Z", shift = At.Shift.AFTER))
    private static void onInit(CallbackInfo ci) {
        TPPConfig.Default.INSTANCE = new TPPConfigImplNeoForge();
    }
}
