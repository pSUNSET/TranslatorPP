package net.psunset.translatorpp;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.psunset.translatorpp.keybind.TPPKeyMappings;
import org.slf4j.Logger;

public final class TranslatorPP {
    public static final String ID = "translatorpp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void commonInit() {
    }

    @Environment(EnvType.CLIENT)
    public static void clientInit() {
        TPPKeyMappings.init();
    }
}
