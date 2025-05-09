package net.psunset.translatorpp.fabric.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.fabricmc.loader.impl.discovery.ModResolutionException;
import net.psunset.translatorpp.compat.CompatUtl;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;

public class TPPModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CompatUtl.isClothConfigLoaded() ?
                TPPConfigImplCloth::screen :
                parent -> { throw new IllegalStateException("misc.translator.missing.clothconfig"); };
    }
}
