package net.psunset.translatorpp.translation;

public abstract class AbstractTranslationClientTool {
    public static String ERROR = "!@#$%^&*()_+";

    public AbstractTranslationClientTool() {
    }

    public abstract String translate(String q, String sl, String tl) throws Exception;
}
