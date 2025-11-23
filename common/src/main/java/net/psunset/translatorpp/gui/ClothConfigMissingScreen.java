package net.psunset.translatorpp.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ClothConfigMissingScreen extends Screen {

    private static final String MISSING_DESC_PREFIX = "gui.translatorpp.missing.clothconfig.";

    private final Screen lastScreen;

    public ClothConfigMissingScreen(Screen lastScreen) {
        super(Component.translatable("gui.title.translatorpp.missing.clothconfig"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, CommonComponents.GUI_BACK, this::onBack));
    }

    public void onBack(Button button) {
        this.onClose();
    }

    @Override
    public void resize(Minecraft minecraft, int i, int j) {
        this.init(minecraft, i, j);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, i, j, f);
        GuiComponent.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, -1);
        int line = 0;
        String key;
        while (I18n.exists(key = MISSING_DESC_PREFIX + line)) {
            GuiComponent.drawCenteredString(poseStack, this.font, Component.translatable(key), this.width / 2, this.height / 4 + 60 + line * 12, -1);
            ++line;
        }
    }
}
