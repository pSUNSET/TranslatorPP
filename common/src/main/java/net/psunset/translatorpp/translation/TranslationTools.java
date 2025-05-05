package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;
import net.minecraft.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum TranslationTools {
    GoogleTranslation("Google Translation", GoogleTranslationClientTool::getInstance),
    OpenAIClient("OpenAI Client", OpenAIClientTool::getInstance);

    public static final Map<String, TranslationTools> entries = Util.make(Maps.newHashMap(), map -> {
        Arrays.asList(values()).forEach(it -> map.put(it.displayName, it));
    });

    private final String displayName;
    private final Supplier<? extends AbstractTranslationClientTool> toolSup;

    TranslationTools(String displayName, Supplier<? extends AbstractTranslationClientTool> toolSup) {
        this.displayName = displayName;
        this.toolSup = toolSup;
    }

    public String getDisplayName() {
        return displayName;
    }

    public AbstractTranslationClientTool getTool() {
        return toolSup.get();
    }
}
