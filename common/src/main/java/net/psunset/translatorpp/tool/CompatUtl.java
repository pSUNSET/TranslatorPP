package net.psunset.translatorpp.tool;

import dev.architectury.platform.Platform;

public class CompatUtl {
    public static class ClothConfig {
        public static boolean isLoaded() {
            return Platform.isForgeLike() ? Platform.isModLoaded("cloth_config") : Platform.isModLoaded("cloth-config");
        }
    }
}
