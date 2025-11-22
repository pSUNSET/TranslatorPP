package net.psunset.translatorpp.compat.clothconfig.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.api.ScreenProvider;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public class TPPConfigClothScreen extends Screen {

    private static final String TITLE = "config.title.translatorpp";
    private static final String CATEGORY_PREFIX = "config.category.translatorpp.";
    private static final String TOOLTIP = "config.translatorpp.category.tooltip";

    private final ScreenProvider[] configs;
    protected final Screen lastScreen;

    // If there is only one config type (and it can be edited, we show that instantly on the way "down" and want to close on the way "up".
    // But when returning from the restart/reload confirmation screens, we need to stay open.
    private boolean autoClose = false;

    public TPPConfigClothScreen(final Screen parent) {
        this(parent, TPPConfigImplCloth.configs());
    }

    /**
     * Used by NeoForge-sided edition.
     * @see net.psunset.translatorpp.compat.clothconfig.neoforge.TPPConfigClothScreenNeoForge
     */
    @ApiStatus.Internal
    public TPPConfigClothScreen(final Screen lastScreen, ScreenProvider[] configs) {
        super(Component.translatable(TITLE));
        this.lastScreen = lastScreen;
        this.configs = configs;
    }

    @Override
    protected void init() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingBottom(6).alignHorizontallyCenter();
        GridLayout.RowHelper rowHelper = gridLayout.createRowHelper(1);
        Button btn;
        for (final ScreenProvider config : this.configs) {
            String configName = config.getClass().getSimpleName().toLowerCase();
            String displayName = I18n.get(CATEGORY_PREFIX + configName);
            btn = Button.builder(Component.literal("%s...".formatted(displayName)),
                    button -> minecraft.setScreen(config.createScreen(this))).width(310).build();
            btn.setTooltip(Tooltip.create(Component.translatable(TOOLTIP, displayName)));
            rowHelper.addChild(btn);
        }
        gridLayout.arrangeElements();
        FrameLayout.alignInRectangle(gridLayout, 0, this.height / 6, this.width, this.height, 0.5F, 0.0F);
        gridLayout.visitWidgets(this::addRenderableWidget);
        this.createFooter();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(guiGraphics, i, j, f);
    }

    @Override
    public void added() {
        super.added();
        if (autoClose) {
            autoClose = false;
            onClose();
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(lastScreen);
    }

    /**
     * [Vanilla Copy] {@link SimpleOptionsSubScreen#createFooter()}
     */
    protected void createFooter() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, this::onDone).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    private void onDone(Button button) {
        this.onClose();
    }
}
