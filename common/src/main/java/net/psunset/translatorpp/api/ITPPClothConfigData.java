package net.psunset.translatorpp.api;

import me.shedaniel.autoconfig.ConfigData;
import net.minecraft.client.gui.screens.Screen;

/**
 * A {@link ConfigData} with a {@link Screen} creator method.
 * Do NOT use this if Cloth Config is not installed.
 */
public interface ITPPClothConfigData extends ConfigData, IScreenProvider {
}
