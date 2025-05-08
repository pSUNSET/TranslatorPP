package net.psunset.translatorpp.fabric.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;

public class TPPModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return TPPConfigImplCloth::screen;
    }
}
