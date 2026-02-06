package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.psunset.translatorpp.api.ComponentizableEnum;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface TranslationTool {
    String ERROR = "!@#$%^&*()_+";

    String translate(String q, String sl, String tl) throws Exception;

    enum Type implements ComponentizableEnum {
        GoogleTranslation("Google Translation", GoogleTranslationTool.INSTANCE),
        OpenAIClient("OpenAI Client", OpenAIClientTool.INSTANCE);

        public static final Map<String, Type> entries = Util.make(Maps.newHashMap(), map -> {
            for (Type type : values()) map.put(type.displayName, type);
        });

        public final String displayName;
        public final TranslationTool tool;

        Type(String displayName, TranslationTool tool) {
            this.displayName = displayName;
            this.tool = tool;
        }

        @Override
        public @NotNull Component toComponent() {
            return Component.literal(this.displayName);
        }
    }
}
