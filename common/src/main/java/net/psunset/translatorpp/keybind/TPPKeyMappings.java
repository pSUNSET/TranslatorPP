package net.psunset.translatorpp.keybind;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class TPPKeyMappings {

    private static final ImmutableSet.Builder<KeyMapping> ENTRIES = ImmutableSet.builder();

    public static final KeyMapping TRANSLATE_KEY = register(
            "key.translatorpp.translate",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.category.translatorpp.general"
    );

    public static KeyMapping CLOTH_CONFIG_KEY = register(
            "key.translatorpp.config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.category.translatorpp.general"
    );

    private static KeyMapping register(String translation, InputConstants.Type type, int keyCode, String category) {
        var key = new KeyMapping(translation, type, keyCode, category);
        ENTRIES.add(key);
        return key;
    }

    private static KeyMapping register(String translation, int keyCode, String category) {
        var key = new KeyMapping(translation, keyCode, category);
        ENTRIES.add(key);
        return key;
    }

    public static ImmutableSet<KeyMapping> getEntries() {
        return ENTRIES.build();
    }
}