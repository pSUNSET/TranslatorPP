package net.psunset.translatorpp.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class TPPKeyMappings {
    public static final KeyMapping TRANSLATE_KEY = new KeyMapping(
            "key.translatorpp.translate",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.categories.translatorpp.general"
    );

    public static void init() {
        KeyMappingRegistry.register(TRANSLATE_KEY);
    }
}
