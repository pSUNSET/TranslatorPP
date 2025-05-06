package net.psunset.translatorpp.fabric.config;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LazyDropdownMenuBuilder<T> extends DropdownMenuBuilder<T> {

    protected Supplier<ImmutableList<T>> selectionsSup = ImmutableList::of;

    public LazyDropdownMenuBuilder(Component resetButtonKey, Component fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        super(resetButtonKey, fieldNameKey, topCellElement, cellCreator);
        this.selections = null;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setSelections(Iterable<T> selections) {
        return setSelectionsSupplier(() -> ImmutableList.copyOf(selections));
    }

    public LazyDropdownMenuBuilder<T> setSelections(ImmutableList<T> selections) {
        return setSelectionsSupplier(() -> selections);
    }

    public LazyDropdownMenuBuilder<T> setSelectionsSupplier(Supplier<ImmutableList<T>> selectionsSup) {
        this.selectionsSup = selectionsSup;
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setDefaultValue(Supplier<T> defaultValue) {
        super.setDefaultValue(defaultValue);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setDefaultValue(T defaultValue) {
        super.setDefaultValue(defaultValue);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setSaveConsumer(Consumer<T> saveConsumer) {
        super.setSaveConsumer(saveConsumer);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setTooltipSupplier(Supplier<Optional<Component[]>> tooltipSupplier) {
        super.setTooltipSupplier(tooltipSupplier);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setTooltipSupplier(Function<T, Optional<Component[]>> tooltipSupplier) {
        super.setTooltipSupplier(tooltipSupplier);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setTooltip(Optional<Component[]> tooltip) {
        super.setTooltip(tooltip);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setTooltip(Component... tooltip) {
        super.setTooltip(tooltip);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> requireRestart() {
        super.requireRestart();
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setErrorSupplier(Function<T, Optional<Component>> errorSupplier) {
        super.setErrorSupplier(errorSupplier);
        return this;
    }

    @Override
    public LazyDropdownMenuBuilder<T> setSuggestionMode(boolean suggestionMode) {
        super.setSuggestionMode(suggestionMode);
        return this;
    }

    @Override
    public @NotNull LazyDropdownBoxEntry<T> build() {
        LazyDropdownBoxEntry<T> entry = new LazyDropdownBoxEntry<>(this.getFieldNameKey(), this.getResetButtonKey(), null, this.isRequireRestart(), this.defaultValue, this.saveConsumer, this.selectionsSup, this.topCellElement, this.cellCreator);
        entry.setTooltipSupplier(() -> this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> this.errorSupplier.apply(entry.getValue()));
        }
        entry.setSuggestionMode(this.suggestionMode);
        return (LazyDropdownBoxEntry<T>) this.finishBuilding(entry);
    }

    public static <T> LazyDropdownMenuBuilder<T> start(ConfigEntryBuilder configBuilder, Component fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        return new LazyDropdownMenuBuilder<>(configBuilder.getResetButtonKey(), fieldNameKey, topCellElement, cellCreator);
    }
}
