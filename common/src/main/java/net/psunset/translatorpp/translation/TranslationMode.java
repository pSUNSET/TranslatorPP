package net.psunset.translatorpp.translation;

public enum TranslationMode {
    /**
     * Only translates display name of item.
     */
    NAME_ONLY,

    /**
     * Translates all lines.
     * Except for the result of name will follow the original name line,
     * the others will be shown at the end of the raw texts.
     */
    NAME_TOP,

    /**
     * Translates all lines.
     * Every translated result of a line will follow the original line.
     */
    LINE_BY_LINE,

    /**
     * Translates all lines.
     * All translated results will be shown at the end of raw texts.
     */
    ALL_IN_END,
}
