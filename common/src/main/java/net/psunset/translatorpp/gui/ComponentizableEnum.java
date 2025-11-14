package net.psunset.translatorpp.gui;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * An enum that can be converted to a {@link Component}.
 * Should only be implemented by enum classes.
 * In NeoForge side, will extend {@link net.neoforged.neoforge.common.TranslatableEnum} by mixin injection.
 */
public interface ComponentizableEnum {
    /**
     * Converts self to a {@link Component}.
     * Defaults to a literal component with the {@link Enum#name()}.
     */
    default @NotNull Component toComponent() {
        return Component.literal(((Enum<?>) this).name());
    }
}
