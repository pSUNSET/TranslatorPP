package net.psunset.translatorpp.tool;

import net.psunset.translatorpp.platform.Platform;

public class CompatUtl {
    public static class ClothConfig {
        public static boolean isLoaded() {
            return Platform.isModLoaded(Platform.isNeoForge() ? "cloth_config" : "cloth-config");
        }
    }

    public static class JEI {
        public static boolean isLoaded() {
            return Platform.isModLoaded("jei");
        }
    }

    public static class REI {
        public static boolean isLoaded() {
            return Platform.isModLoaded("roughlyenoughitems");
        }
    }

    public static class Jade {
        public static boolean isLoaded() {
            return Platform.isModLoaded("jade");
        }
    }
}
