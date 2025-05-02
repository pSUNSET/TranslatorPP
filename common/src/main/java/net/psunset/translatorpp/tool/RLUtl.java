package net.psunset.translatorpp.tool;

import net.minecraft.resources.ResourceLocation;
import net.psunset.translatorpp.TranslatorPP;

public class RLUtl {
    public static ResourceLocation of(String name) {
        return ResourceLocation.fromNamespaceAndPath(TranslatorPP.ID, name);
    }

    public static ResourceLocation ofVanilla(String name) {
        return ResourceLocation.withDefaultNamespace(name);
    }
}
