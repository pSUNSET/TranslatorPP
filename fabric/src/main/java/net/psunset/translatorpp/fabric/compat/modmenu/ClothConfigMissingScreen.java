package net.psunset.translatorpp.fabric.compat.modmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ClothConfigMissingScreen extends Screen {

    private static final String MISSING_DESC_PREFIX = "gui.translatorpp.missing.clothconfig.";
    private static final int MISSING_DESC_LINE = 2;

    private final Screen lastScreen;
    private Button backButton;

    public ClothConfigMissingScreen(Screen lastScreen) {
        super(Component.translatable("gui.title.translatorpp.missing.clothconfig"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.backButton = this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_BACK, button -> this.onBack())
                        .bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20)
                        .build()
        );
    }

    public void onBack() {
        onClose();
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
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
        for (int k = 0; k < MISSING_DESC_LINE; k++) {
            guiGraphics.drawCenteredString(this.font, Component.translatable(MISSING_DESC_PREFIX + k), this.width / 2, this.height / 4 + 60 + k * 12, 16777215);
        }
    }
}
