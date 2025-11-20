package net.psunset.translatorpp.mixin.neoforge;

import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.TPPConfig;
import net.psunset.translatorpp.config.neoforge.TPPConfigImplNeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TPPConfig.class)
public interface TPPConfigMixin {

    @Overwrite
    static void init() {
        TranslatorPP.LOGGER.debug("NeoForge is loaded, using neoforge for Translator++ Config.");
        TPPConfig.Dummy.INSTANCE = new TPPConfigImplNeoForge();
        // init is completed in mod constructor, no need to call here
//        TPPConfigImplNeoForge.init(container);
    }
}
