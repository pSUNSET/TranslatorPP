package net.psunset.translatorpp.compat;

import dev.architectury.platform.Platform;

public class CompatUtl {
    public static boolean isClothConfigLoaded() {
        return Platform.isForgeLike() ? Platform.isModLoaded("cloth_config") : Platform.isModLoaded("cloth-config");
    }
}
