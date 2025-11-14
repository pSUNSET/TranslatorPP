package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.gui.ComponentizableEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public interface TranslationTool {
    String ERROR = "!@#$%^&*()_+";

    String translate(String q, String sl, String tl) throws Exception;

    enum Type implements ComponentizableEnum {
        GoogleTranslation("Google Translation", GoogleTranslationTool::getInstance),
        OpenAIClient("OpenAI Client", OpenAIClientTool::getInstance);

        public static final Map<String, Type> entries = Util.make(Maps.newHashMap(), map -> {
            for (Type type : values()) map.put(type.displayName, type);
        });

        public final String displayName;
        private final Supplier<? extends TranslationTool> toolSup;

        Type(String displayName, Supplier<? extends TranslationTool> toolSup) {
            this.displayName = displayName;
            this.toolSup = toolSup;
        }

        public TranslationTool getTool() {
            return toolSup.get();
        }

        @Override
        public @NotNull Component toComponent() {
            return Component.literal(this.displayName);
        }
    }
}
