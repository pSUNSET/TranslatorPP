package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.function.Supplier;

public class TranslationTools {

    public static HashMap<String, Supplier<? extends AbstractTranslationClientTool>> nameToTool;

    static {
        nameToTool = Maps.newHashMap();
        nameToTool.put("google-translation", GoogleTranslationClientTool::new);
        nameToTool.put("openai", OpenAIClientTool::new);
    }
}
