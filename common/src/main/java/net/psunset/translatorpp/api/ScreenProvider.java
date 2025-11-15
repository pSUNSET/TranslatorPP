package net.psunset.translatorpp.api;

import me.shedaniel.autoconfig.ConfigData;
import net.minecraft.client.gui.screens.Screen;

/**
 * Obviously, a {@link Screen} provider.
 * It is also used to be a simple {@link ConfigData} decoy that provides a {@link Screen} without containing any data.
 */
public interface ScreenProvider {
    /**
     * Create a {@link Screen}.
     * @param parent Often used to go back to the original screen.
     */
    Screen createScreen(Screen parent);
}
