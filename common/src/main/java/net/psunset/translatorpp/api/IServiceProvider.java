package net.psunset.translatorpp.api;

/**
 * Provides a service that translates a query text.
 */
public interface IServiceProvider {
    String translate(String q, String sl, String tl) throws Exception;

    /**
     * Returns the separator used to join multiple query texts into a single string for translation.
     * Defaults to "{NL}", which represents a newline character.
     * It is overridable because Libre Translate seems didn't recognize this.
     */
    default String separator() {
        return "{NL}";
    }
}
