package net.psunset.translatorpp.fabric.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class TPPKeyMappingsFabric {
    public static KeyMapping CONFIG_KEY = new KeyMapping(
            "key.translatorpp.config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.translatorpp.general"
    );

    @Environment(EnvType.CLIENT)
    public static void init() {
        KeyBindingHelper.registerKeyBinding(CONFIG_KEY);
    }
}
