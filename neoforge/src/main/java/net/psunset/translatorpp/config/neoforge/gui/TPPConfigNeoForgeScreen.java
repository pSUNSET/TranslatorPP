package net.psunset.translatorpp.config.neoforge.gui;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.psunset.translatorpp.TranslatorPP;
import net.psunset.translatorpp.config.neoforge.TPPConfigImplNeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This mod has only one section,
 * so it extends section screen directly.
 */
public class TPPConfigNeoForgeScreen extends ConfigurationScreen.ConfigurationSectionScreen {

    private static final Component TITLE = Component.translatable("config.title.translatorpp");

    private static ModConfig modConfig;
    private static final ModConfig.Type modConfigType = ModConfig.Type.CLIENT;  // This mod is client-sided only.

    protected TPPConfigNeoForgeScreen(Screen parent, ModConfig.Type type, ModConfig modConfig) {
        super(parent, type, modConfig, TITLE);
    }

    protected TPPConfigNeoForgeScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Filter filter) {
        super(parent, type, modConfig, TITLE, filter);
    }

    protected TPPConfigNeoForgeScreen(Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends UnmodifiableConfig.Entry> entrySet) {
        super(parentContext, parent, valueSpecs, key, entrySet, TITLE);
    }

    protected TPPConfigNeoForgeScreen(Context context, Component title) {
        super(context, title);
    }

    @Override
    protected @NotNull ConfigurationScreen.ConfigurationSectionScreen rebuild() {
        if (list != null) { // this may be called early, skip and wait for init() then
            list.children().clear();
            boolean hasUndoableElements = false;

            final List<@Nullable Element> elements = new ArrayList<>();
            for (final UnmodifiableConfig.Entry entry : context.entries()) {
                final String key = entry.getKey();
                final Object rawValue = entry.getRawValue();
                switch (entry.getRawValue()) {
                    case ModConfigSpec.ConfigValue cv -> {
                        var valueSpec = getValueSpec(key);
                        var element = switch (valueSpec) {
                            case ModConfigSpec.ListValueSpec listValueSpec -> createList(key, listValueSpec, cv);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof String ->
                                    createStringValue(key, valueSpec::test, () -> (String) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Integer ->
                                    createIntegerValue(key, valueSpec, () -> (Integer) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Long ->
                                    createLongValue(key, valueSpec, () -> (Long) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Double ->
                                    createDoubleValue(key, valueSpec, () -> (Double) cv.getRaw(), cv::set);
                            case ModConfigSpec.ValueSpec spec when cv.getClass() == ModConfigSpec.ConfigValue.class && spec.getDefault() instanceof Enum<?> ->
                                    createEnumValue(key, valueSpec, (Supplier) cv::getRaw, (Consumer) cv::set);
                            case null -> null;

                            default -> switch (cv) {
                                case ModConfigSpec.BooleanValue value ->
                                        createBooleanValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.IntValue value ->
                                        createIntegerValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.LongValue value ->
                                        createLongValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.DoubleValue value ->
                                        createDoubleValue(key, valueSpec, value::getRaw, value::set);
                                case ModConfigSpec.EnumValue value ->
                                        createEnumValue(key, valueSpec, (Supplier) value::getRaw, (Consumer) value::set);
                                default -> createOtherValue(key, cv);
                            };
                        };
                        elements.add(context.filter().filterEntry(context, key, element));
                    }
                    case UnmodifiableConfig subsection when context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig ->
                            elements.add(createSection(key, subconfig, subsection));
                    default ->
                            elements.add(context.filter().filterEntry(context, key, createOtherSection(key, rawValue)));
                }
            }
            elements.addAll(createSyntheticValues());

            for (final Element element : elements) {
                if (element != null) {
                    if (element.name() == null) {
                        list.addSmall(new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty(), font), element.getWidget(options));
                    } else {
                        // "config.translatorpp.".length() = 20
                        var keySuffix = ((TranslatableContents) element.name().getContents()).getKey().substring(20);

                        // Add category header
                        if (TPPConfigImplNeoForge.FIRST_CHILD_TO_CATEGORY.containsKey(keySuffix)) {
                            list.addSmall(new StringWidget(ConfigurationScreen.BIG_BUTTON_WIDTH, Button.DEFAULT_HEIGHT,
                                    Component.translatable("config.category.translatorpp." + TPPConfigImplNeoForge.FIRST_CHILD_TO_CATEGORY.get(keySuffix))
                                            .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD), font), null);
                        }

                        final StringWidget label = new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, element.name(), font);
                        label.alignLeft().setTooltip(Tooltip.create(element.tooltip()));
                        list.addSmall(label, element.getWidget(options));
                    }
                    hasUndoableElements |= element.undoable();
                }
            }

            if (hasUndoableElements && undoButton == null) {
                createUndoButton();
                createResetButton();
            }
        }
        return this;
    }

    /**
     * Returns a new instance of the config screen.
     */
    public static Screen create(Screen parent) {
        if (modConfig == null) {
            for (final ModConfig config : ModConfigs.getConfigSet(ModConfig.Type.CLIENT)) {
                if (config.getModId().equals(TranslatorPP.ID)) {
                    modConfig = config;
                    break;
                }
            }
        }
        return new TPPConfigNeoForgeScreen(parent, modConfigType, modConfig);
    }
}
