package net.psunset.translatorpp.translation;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.psunset.translatorpp.tool.RLUtl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TranslationTools {

    public static HashMap<ResourceLocation, Supplier<? extends AbstractTranslationClientTool>> nameToTool =
            Util.make(new HashMap<>(), map -> {
                map.put(RLUtl.of("google-translation"), GoogleTranslationClientTool::getInstance);
                map.put(RLUtl.of("openai-client"), OpenAIClientTool::getInstance);
            });

    /**
     * Maybe this project will be an API, so create a registry here.
     */
    public static void register(ResourceLocation location, Supplier<? extends  AbstractTranslationClientTool> toolSup) {
        nameToTool.put(location, toolSup);
    }
}
