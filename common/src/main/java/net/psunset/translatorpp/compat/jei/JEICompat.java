package net.psunset.translatorpp.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.inputs.IJeiGuiEventListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.psunset.translatorpp.tool.RLUtl;

@JeiPlugin
public class JEICompat implements IModPlugin {

    @Environment(EnvType.CLIENT)
    public static void init() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return RLUtl.of("jei_compat");
    }
}
