package net.psunset.translatorpp.tool;

import net.minecraft.resources.Identifier;
import net.psunset.translatorpp.TranslatorPP;

public final class IdUtl {
    /**
     * Create a Identifier with TranslatorPP's namespace.
     */
    public static Identifier of(String name) {
        return Identifier.fromNamespaceAndPath(TranslatorPP.ID, name);
    }

    /**
     * Create a Identifier with the vanilla, {@code Minecraft}, namespace.
     */
    public static Identifier ofVanilla(String name) {
        return Identifier.withDefaultNamespace(name);
    }
}
