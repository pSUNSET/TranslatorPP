package net.psunset.translatorpp.fabric.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.psunset.translatorpp.compat.clothconfig.gui.TPPConfigClothScreen;
import net.psunset.translatorpp.fabric.platform.PlatformFabric;
import net.psunset.translatorpp.tool.CompatUtl;

public class TPPModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        PlatformFabric.init(); // Prevent potential issues if ModMenu is loaded before Translator++.
        return CompatUtl.ClothConfig.isLoaded() ? TPPConfigClothScreen::new : ClothConfigMissingScreen::new;
    }
}
