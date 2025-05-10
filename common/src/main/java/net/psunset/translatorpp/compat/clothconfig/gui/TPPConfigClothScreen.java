package net.psunset.translatorpp.compat.clothconfig.gui;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.psunset.translatorpp.compat.clothconfig.TPPClothConfigDataDecoy;
import net.psunset.translatorpp.compat.clothconfig.TPPConfigImplCloth;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
public class TPPConfigClothScreen extends OptionsSubScreen {

    private static final String TITLE = "config.title.translatorpp";
    private static final String CATEGORY_PREFIX = "config.category.translatorpp.";
    private static final String TOOLTIP = "config.translatorpp.category.tooltip";

    private final ImmutableList<TPPClothConfigDataDecoy> configs;
    // If there is only one config type (and it can be edited, we show that instantly on the way "down" and want to close on the way "up".
    // But when returning from the restart/reload confirmation screens, we need to stay open.
    private boolean autoClose = false;

    public TPPConfigClothScreen(final Screen parent) {
        this(parent, TPPConfigImplCloth.configs());
    }

    /**
     * For NeoForge-sided edition, {@link net.psunset.translatorpp.neoforge.compat.clothconfig.gui.TPPConfigClothScreenNeoForge}.
     */
    @ApiStatus.Internal
    public TPPConfigClothScreen(final Screen parent, Iterable<? extends TPPClothConfigDataDecoy> configs) {
        super(parent, Minecraft.getInstance().options, Component.translatable(TITLE));
        this.configs = ImmutableList.copyOf(configs);
    }

    @Override
    protected void addOptions() {
        Button btn = null;
        int count = 0;
        for (final TPPClothConfigDataDecoy config : this.configs) {
            String configName = config.getClass().getSimpleName().toLowerCase();
            String displayName = I18n.get(CATEGORY_PREFIX + configName);
            btn = Button.builder(Component.literal("%s...".formatted(displayName)),
                    button -> minecraft.setScreen(config.createScreen(this))).width(310).build();
            btn.setTooltip(Tooltip.create(Component.translatable(TOOLTIP, displayName)));
            list.addSmall(btn, null);
            count++;
        }

        // In fact, this is completely unaccessible
        if (count == 1) {
            autoClose = true;
            btn.onPress();
        }
    }

    @Override
    public void added() {
        super.added();
        if (autoClose) {
            autoClose = false;
            onClose();
        }
    }
}
