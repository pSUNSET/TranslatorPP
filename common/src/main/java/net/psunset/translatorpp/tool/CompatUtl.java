package net.psunset.translatorpp.tool;

import net.psunset.translatorpp.platform.Platform;

/**
 * Involves utility classes for checking the presence of various mods for compatibility purposes.
 */
public interface CompatUtl {

    final class ClothConfig {
        public static boolean isLoaded() {
            return Platform.isModLoaded(Platform.isForge() ? "cloth_config" : "cloth-config");
        }
    }

    final class JEI {
        public static boolean isLoaded() {
            return Platform.isModLoaded("jei");
        }
    }

    final class REI {
        public static boolean isLoaded() {
            return Platform.isModLoaded("roughlyenoughitems");
        }
    }

    final class Jade {
        public static boolean isLoaded() {
            return Platform.isModLoaded("jade");
        }
    }
}
