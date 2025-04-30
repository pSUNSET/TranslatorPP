package net.psunset.translatorpp.fabric.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.psunset.translatorpp.TranslatorPP;

@Config(name = TranslatorPP.ID)
public class TPPConfig implements ConfigData {

    private static ConfigHolder<TPPConfig> HOLDER;

    public static TPPConfig getInstance() {
        return HOLDER.getConfig();
    }

    public static ConfigHolder<TPPConfig> getHolder() {
        return HOLDER;
    }

    public String sourceLanguage = "auto";
    public String targetLanguage = "es-ES";

    public static void init() {
        HOLDER = AutoConfig.register(TPPConfig.class, GsonConfigSerializer::new);
    }
}
