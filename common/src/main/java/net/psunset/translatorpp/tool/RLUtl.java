package net.psunset.translatorpp.tool;

import net.minecraft.resources.ResourceLocation;
import net.psunset.translatorpp.TranslatorPP;

public final class RLUtl {
    /**
     * Create a ResourceLocation with TranslatorPP's namespace.
     */
    public static ResourceLocation of(String name) {
        return ResourceLocation.fromNamespaceAndPath(TranslatorPP.ID, name);
    }

    /**
     * Create a ResourceLocation with the vanilla, {@code Minecraft}, namespace.
     */
    public static ResourceLocation ofVanilla(String name) {
        return ResourceLocation.withDefaultNamespace(name);
    }
}
