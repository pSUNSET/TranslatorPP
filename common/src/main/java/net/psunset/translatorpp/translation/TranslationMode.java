package net.psunset.translatorpp.translation;

public enum TranslationMode {
    /**
     * Only translates display name of item.
     */
    NAME_ONLY,

    /**
     * Translates all lines.
     * Every translated result of a line will be shown following line.
     */
    LINE_BY_LINE,

    /**
     * Translates all lines.
     * All translated results will be shown in the end of raw texts.
     */
    ALL_IN_END,
}
