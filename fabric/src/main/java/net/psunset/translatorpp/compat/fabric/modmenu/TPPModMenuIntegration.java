package net.psunset.translatorpp.compat.fabric.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import net.psunset.translatorpp.compat.clothconfig.gui.TPPConfigClothScreenWrapper;
import net.psunset.translatorpp.tool.CompatUtl;

public final class TPPModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CompatUtl.ClothConfig.isLoaded() ? TPPConfigClothScreenWrapper::createScreen : ClothConfigMissingScreen::new;
    }
}
