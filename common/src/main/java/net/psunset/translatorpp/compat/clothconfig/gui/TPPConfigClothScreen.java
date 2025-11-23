package net.psunset.translatorpp.compat.clothconfig.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.api.ScreenProvider;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TPPConfigClothScreen extends Screen {

    private static final String TITLE = "config.title.translatorpp";
    private static final String CATEGORY_PREFIX = "config.category.translatorpp.";
    private static final String TOOLTIP = "config.translatorpp.category.tooltip";

    private final ScreenProvider[] configs;
    protected final Screen lastScreen;

    public TPPConfigClothScreen(final Screen parent) {
        this(parent, TPPConfigImplCloth.configs());
    }

    @ApiStatus.Internal
    public TPPConfigClothScreen(final Screen lastScreen, ScreenProvider[] configs) {
        super(Component.translatable(TITLE));
        this.lastScreen = lastScreen;
        this.configs = configs;
    }

    @Override
    protected void init() {
        int y = this.height / 6;
        for (final ScreenProvider config : this.configs) {
            String configName = config.getClass().getSimpleName().toLowerCase();
            String displayName = I18n.get(CATEGORY_PREFIX + configName);
            Component component = Component.translatable(TOOLTIP, displayName);
            this.addRenderableWidget(
                    new Button(this.width / 2 - 155, y, 310, 20, Component.literal("%s...".formatted(displayName)),
                            button -> minecraft.setScreen(config.createScreen(this)),
                            new Button.OnTooltip() {
                                @Override
                                public void onTooltip(Button button, PoseStack poseStack, int i, int j) {
                                    TPPConfigClothScreen.this.renderTooltip(poseStack, component, i, j);
                                }

                                @Override
                                public void narrateTooltip(Consumer<Component> consumer) {
                                    consumer.accept(component);
                                }
                            }));
            y += 26;
        }
        this.createFooter();
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        GuiComponent.drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 16777215);
        super.render(poseStack, i, j, f);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }

    /**
     * [Vanilla Copy] {@link SimpleOptionsSubScreen#createFooter()}
     */
    protected void createFooter() {
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, this::onDone));
    }

    private void onDone(Button button) {
        this.onClose();
    }
}
