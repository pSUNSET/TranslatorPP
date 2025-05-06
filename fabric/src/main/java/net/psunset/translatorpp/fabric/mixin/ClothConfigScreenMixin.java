package net.psunset.translatorpp.fabric.mixin;

import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.fabric.config.TPPConfig;
import net.psunset.translatorpp.fabric.translation.TranslationKit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ClothConfigScreen.class)
public abstract class ClothConfigScreenMixin extends AbstractTabbedConfigScreen {

    @Shadow
    @Final
    private List<Tuple<Component, Integer>> tabs;
    @Unique
    private int delaySelectedCategoryIndex = selectedCategoryIndex;

    protected ClothConfigScreenMixin(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    @Shadow
    public abstract Component getSelectedCategory();

    @Unique
    public Component getDelaySelectedCategory() {
        return (Component)((Tuple)this.tabs.get(this.delaySelectedCategoryIndex)).getA();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void afterRender(GuiGraphics graphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.delaySelectedCategoryIndex != selectedCategoryIndex) {
            String selectedCategoryText = getSelectedCategory().getString();
            String delaySelectedCategoryText = getDelaySelectedCategory().getString();
            TranslatorPP.LOGGER.info("Cloth Config selected tag got changed from {} to {}", delaySelectedCategoryText, selectedCategoryText);
            if (delaySelectedCategoryText.equals(I18n.get("config.category.translatorpp.general"))) {
                TranslationKit.refreshOpenAIClientTool();
                TranslationKit.getInstance().clearCache();
            } else if (delaySelectedCategoryText.equals(I18n.get("config.category.translatorpp.openai"))) {
                TranslationKit.refreshOpenAIClientTool();
                TPPConfig.refreshOpenAIModels();
            }
            this.delaySelectedCategoryIndex = selectedCategoryIndex;
        }
    }
}
