package net.psunset.translatorpp.compat.clothconfig;

import me.shedaniel.autoconfig.ConfigData;
import net.minecraft.client.gui.screens.Screen;

/**
 * A simple {@link ConfigData} decoy that provides a {@link Screen} without containing any data.
 */
public interface TPPClothConfigDataDecoy {
    Screen createScreen(Screen parent);
}
