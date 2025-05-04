package net.psunset.translatorpp.translation;

import java.util.function.Supplier;

public enum TranslationTools {
    GoogleTranslation(GoogleTranslationClientTool::getInstance),
    OpenAIClient(OpenAIClientTool::getInstance);

    private final Supplier<? extends AbstractTranslationClientTool> toolSup;

    TranslationTools(Supplier<? extends AbstractTranslationClientTool> toolSup) {
        this.toolSup = toolSup;
    }

    public AbstractTranslationClientTool getTool() {
        return toolSup.get();
    }
}
