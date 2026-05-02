package net.psunset.translatorpp.compat.clothconfig.gui;

import net.minecraft.client.gui.screens.Screen;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;

/**
 * A class with a screen creator.
 * Without any cloth config class import, it avoids crash in runtime.
 */
public class TPPConfigClothScreenWrapper {
    public static Screen createScreen(Screen parent) {
        return TPPConfigImplCloth.config().createScreen(parent);
    }
}
