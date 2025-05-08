package net.psunset.translatorpp.neoforge.config.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Locale;

public class TPPConfigNeoForgeScreen extends OptionsSubScreen {

    private static final String NEOFORGE_LANG_PREFIX = "neoforge.configuration.uitext.";
    private static final String TITLE = "config.title.translatorpp";
    private static final String SUBTITLE_PREFIX = TITLE + ".";
    private static final String CATEGORY_PREFIX = "config.category.translatorpp.";
    private static final String SECTION = NEOFORGE_LANG_PREFIX + "section";
    private static final String FILENAME_TOOLTIP = NEOFORGE_LANG_PREFIX + "filenametooltip";
    private static final ChatFormatting FILENAME_TOOLTIP_STYLE = ChatFormatting.GRAY;

    private static final MutableComponent EMPTY_LINE = Component.literal("\n\n");

    protected static final ConfigurationScreen.TranslationChecker translationChecker = new ConfigurationScreen.TranslationChecker();

    protected final ModContainer mod;
    private final PropertyDispatch.QuadFunction<TPPConfigNeoForgeScreen, ModConfig.Type, ModConfig, Component, Screen> sectionScreen;

    public ModConfigSpec.RestartType needsRestart = ModConfigSpec.RestartType.NONE;
    // If there is only one config type (and it can be edited, we show that instantly on the way "down" and want to close on the way "up".
    // But when returning from the restart/reload confirmation screens, we need to stay open.
    private boolean autoClose = false;

    public TPPConfigNeoForgeScreen(final ModContainer mod, final Screen parent) {
        this(mod, parent, ConfigurationScreen.ConfigurationSectionScreen::new);
    }

    public TPPConfigNeoForgeScreen(final ModContainer mod, final Screen parent, ConfigurationScreen.ConfigurationSectionScreen.Filter filter) {
        this(mod, parent, (a, b, c, d) -> new ConfigurationScreen.ConfigurationSectionScreen(a, b, c, d, filter));
    }

    @SuppressWarnings("resource")
    public TPPConfigNeoForgeScreen(final ModContainer mod, final Screen parent, PropertyDispatch.QuadFunction<TPPConfigNeoForgeScreen, ModConfig.Type, ModConfig, Component, Screen> sectionScreen) {
        super(parent, Minecraft.getInstance().options, Component.translatable(TITLE));
        this.mod = mod;
        this.sectionScreen = sectionScreen;
    }

    @Override
    protected void addOptions() {
        Button btn = null;
        int count = 0;
        for (final ModConfig.Type type : ModConfig.Type.values()) {
//            boolean headerAdded = false;
            for (final ModConfig modConfig : ModConfigs.getConfigSet(type)) {
                if (modConfig.getModId().equals(mod.getModId())) {
                    String configName = modConfig.getFileName().substring(13, modConfig.getFileName().length() - 5);
                    // No need header here
//                    if (!headerAdded) {
//                        list.addSmall(new StringWidget(ConfigurationScreen.BIG_BUTTON_WIDTH, Button.DEFAULT_HEIGHT,
//                                Component.translatable(LANG_PREFIX + type.name().toLowerCase(Locale.ENGLISH)).withStyle(ChatFormatting.UNDERLINE), font).alignLeft(), null);
//                        headerAdded = true;
//                    }
                    btn = Button.builder(Component.translatable(SECTION, Component.translatable(CATEGORY_PREFIX + configName)),
                            button -> minecraft.setScreen(sectionScreen.apply(this, type, modConfig, Component.translatable(SUBTITLE_PREFIX + configName)))).width(ConfigurationScreen.BIG_BUTTON_WIDTH).build();
                    MutableComponent tooltip = Component.empty();
                    if (!((ModConfigSpec) modConfig.getSpec()).isLoaded()) {
                        tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_NOT_LOADED).append(EMPTY_LINE);
                        btn.active = false;
                        count = 99; // prevent autoClose
                    }
                    // This mod is client-sided only.
//                    else if (type == ModConfig.Type.SERVER && minecraft.getCurrentServer() != null && !minecraft.isSingleplayer()) {
//                        tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_ONLINE).append(EMPTY_LINE);
//                        btn.active = false;
//                        count = 99; // prevent autoClose
//                    } else if (type == ModConfig.Type.SERVER && minecraft.hasSingleplayerServer() && minecraft.getSingleplayerServer().isPublished()) {
//                        tooltip.append(ConfigurationScreen.TOOLTIP_CANNOT_EDIT_THIS_WHILE_OPEN_TO_LAN).append(EMPTY_LINE);
//                        btn.active = false;
//                        count = 99; // prevent autoClose
//                    }
                    tooltip.append(Component.translatable(FILENAME_TOOLTIP, modConfig.getFileName()).withStyle(FILENAME_TOOLTIP_STYLE));
                    btn.setTooltip(Tooltip.create(tooltip));
                    list.addSmall(btn, null);
                    count++;
                }
            }
        }
        if (count == 1) {
            autoClose = true;
            btn.onPress();
        }
    }

    public Component translatableConfig(ModConfig modConfig, String suffix, String fallback) {
        return Component.translatable(translationChecker.check(mod.getModId() + ".configuration.section." + modConfig.getFileName().replaceAll("[^a-zA-Z0-9]+", ".").replaceFirst("^\\.", "").replaceFirst("\\.$", "").toLowerCase(Locale.ENGLISH) + suffix, fallback), mod.getModInfo().getDisplayName());
    }

    @Override
    public void added() {
        super.added();
        if (autoClose) {
            autoClose = false;
            onClose();
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void onClose() {
        translationChecker.finish();
        switch (needsRestart) {
            case GAME -> {
                minecraft.setScreen(new TooltipConfirmScreen(b -> {
                    if (b) {
                        minecraft.stop();
                    } else {
                        super.onClose();
                    }
                }, ConfigurationScreen.GAME_RESTART_TITLE, ConfigurationScreen.GAME_RESTART_MESSAGE, ConfigurationScreen.GAME_RESTART_YES, ConfigurationScreen.RESTART_NO));
                return;
            }
            case WORLD -> {
                if (minecraft.level != null) {
                    minecraft.setScreen(new TooltipConfirmScreen(b -> {
                        if (b) {
                            // when changing server configs from the client is added, this is where we tell the server to restart and activate the new config.
                            // also needs a different text in MP ("server will restart/exit, yada yada") than in SP
                            onDisconnect();
                        } else {
                            super.onClose();
                        }
                    }, ConfigurationScreen.SERVER_RESTART_TITLE, ConfigurationScreen.SERVER_RESTART_MESSAGE, minecraft.isLocalServer() ? ConfigurationScreen.RETURN_TO_MENU : CommonComponents.GUI_DISCONNECT, ConfigurationScreen.RESTART_NO));
                    return;
                }
            }
        }
        super.onClose();
    }

    // direct copy from PauseScreen (which has the best implementation), sadly it's not really accessible
    private void onDisconnect() {
        boolean flag = this.minecraft.isLocalServer();
        ServerData serverdata = this.minecraft.getCurrentServer();
        this.minecraft.level.disconnect();
        if (flag) {
            this.minecraft.disconnect(new GenericMessageScreen(ConfigurationScreen.SAVING_LEVEL));
        } else {
            this.minecraft.disconnect();
        }

        TitleScreen titlescreen = new TitleScreen();
        if (flag) {
            this.minecraft.setScreen(titlescreen);
        } else if (serverdata != null && serverdata.isRealm()) {
            this.minecraft.setScreen(new RealmsMainScreen(titlescreen));
        } else {
            this.minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
        }
    }


    private static final class TooltipConfirmScreen extends ConfirmScreen {
        boolean seenYes = false;

        private TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
            super(callback, title, message, yesButton, noButton);
        }

        @Override
        protected void init() {
            seenYes = false;
            super.init();
        }

        @Override
        protected void addExitButton(Button button) {
            if (seenYes) {
                button.setTooltip(Tooltip.create(ConfigurationScreen.RESTART_NO_TOOLTIP));
            } else {
                seenYes = true;
            }
            super.addExitButton(button);
        }
    }
}
