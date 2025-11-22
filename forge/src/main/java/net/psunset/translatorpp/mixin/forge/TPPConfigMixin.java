package net.psunset.translatorpp.mixin.forge;

import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.config.forge.TPPConfigImplForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = TPPConfig.class, remap = false)
public interface TPPConfigMixin {

    @Overwrite
    static void init() {
        TranslatorPP.LOGGER.debug("Forge is loaded, using forge for Translator++ Config.");
        TPPConfig.Dummy.INSTANCE = new TPPConfigImplForge();
        // init is completed in mod constructor, no need to call here
//        TPPConfigImplNeoForge.init(container);
    }
}
