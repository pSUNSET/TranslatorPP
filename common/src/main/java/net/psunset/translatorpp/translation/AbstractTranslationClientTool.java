package net.psunset.translatorpp.translation;

import net.psunset.translatorpp.TranslatorPP;

public abstract class AbstractTranslationClientTool {
    public static String ERROR = "!@#$%^&*()_+";

    public AbstractTranslationClientTool() {
    }

    public String translate(String q, String sl, String tl) {
        try {
            return _translate(q, sl, tl);
        } catch (Exception e) {
            TranslatorPP.LOGGER.error("Error while translating: {}", e.toString());
            return ERROR;
        }
    }

    public abstract String _translate(String q, String sl, String tl) throws Exception;
}
