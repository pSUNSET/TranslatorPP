package net.psunset.translatorpp.core;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.api.ComponentizableEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public enum TranslationMode implements ComponentizableEnum {
    /**
     * Only translates display name of item.
     */
    NAME_ONLY("Name Only"),

    /**
     * Translates all lines.
     * Except for the result of name will follow the original name line,
     * the others will be shown at the end of the raw texts.
     */
    NAME_TOP("Name Top"),

    /**
     * Translates all lines.
     * Every translated result of a line will follow the original line.
     */
    LINE_BY_LINE("Line By Line"),

    /**
     * Translates all lines.
     * All translated results will be shown at the end of raw texts.
     */
    ALL_IN_END("All In End"),

    /**
     * Override all raw contents.
     */
    REPLACE("Replace");

    public final String displayName;

    TranslationMode(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public @NotNull Component toComponent() {
        return Component.literal(this.displayName);
    }
}
