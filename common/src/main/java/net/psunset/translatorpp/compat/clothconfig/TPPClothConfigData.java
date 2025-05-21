package net.psunset.translatorpp.compat.clothconfig;

import me.shedaniel.autoconfig.ConfigData;
import net.minecraft.client.gui.screens.Screen;
import net.psunset.translatorpp.gui.ScreenProvider;

/**
 * A {@link ConfigData} with a {@link Screen} creator method.
 */
public interface TPPClothConfigData extends ConfigData, ScreenProvider {
}
