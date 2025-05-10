package net.psunset.translatorpp.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.psunset.translatorpp.tool.CompatUtl;
import org.lwjgl.glfw.GLFW;

public class TPPKeyMappings {
    public static final KeyMapping TRANSLATE_KEY = new KeyMapping(
            "key.translatorpp.translate",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.categories.translatorpp.general"
    );

    public static KeyMapping CLOTH_CONFIG_KEY = new KeyMapping(
            "key.translatorpp.config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.translatorpp.general"
    );

    @Environment(EnvType.CLIENT)
    public static void init() {
        KeyMappingRegistry.register(TRANSLATE_KEY);
        KeyMappingRegistry.register(CLOTH_CONFIG_KEY);
    }
}
