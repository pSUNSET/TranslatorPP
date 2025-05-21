package net.psunset.translatorpp.gui;

import me.shedaniel.autoconfig.ConfigData;
import net.minecraft.client.gui.screens.Screen;

/**
 * Obviously, a {@link Screen} provider.
 * It is used to be a simple {@link ConfigData} decoy that provides a {@link Screen} without containing any data.
 */
public interface ScreenProvider {
    Screen createScreen(Screen parent);
}
