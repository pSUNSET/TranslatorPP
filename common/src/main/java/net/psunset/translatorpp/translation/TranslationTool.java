package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;
import net.minecraft.Util;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public interface TranslationTool {
    String ERROR = "!@#$%^&*()_+";

    String translate(String q, String sl, String tl) throws Exception;

    enum Type {
        GoogleTranslation("Google Translation", GoogleTranslationTool::getInstance),
        OpenAIClient("OpenAI Client", OpenAIClientTool::getInstance);

        public static final Map<String, Type> entries = Util.make(Maps.newHashMap(), map -> {
            Arrays.asList(values()).forEach(it -> map.put(it.displayName, it));
        });

        private final String displayName;
        private final Supplier<? extends TranslationTool> toolSup;

        Type(String displayName, Supplier<? extends TranslationTool> toolSup) {
            this.displayName = displayName;
            this.toolSup = toolSup;
        }

        public String getDisplayName() {
            return displayName;
        }

        public TranslationTool getTool() {
            return toolSup.get();
        }
    }
}
