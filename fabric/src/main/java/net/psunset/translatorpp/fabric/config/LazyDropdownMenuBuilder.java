package net.psunset.translatorpp.fabric.config;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LazyDropdownMenuBuilder<T> extends DropdownMenuBuilder<T> {

    protected Supplier<Iterable<T>> selectionsSup = Collections::emptyList;

    public LazyDropdownMenuBuilder(Component resetButtonKey, Component fieldNameKey, DropdownBoxEntry.SelectionTopCellElement<T> topCellElement, DropdownBoxEntry.SelectionCellCreator<T> cellCreator) {
        super(resetButtonKey, fieldNameKey, topCellElement, cellCreator);
        this.selections = null;
    }

    @Override
    public DropdownMenuBuilder<T> setSelections(Iterable<T> selections) {
        return setSelectionsSupplier(() -> selections);
    }

    public DropdownMenuBuilder<T> setSelectionsSupplier(Supplier<Iterable<T>> selectionsSup) {
        this.selectionsSup = selectionsSup;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull LazyDropdownBoxEntry<T> build() {
        LazyDropdownBoxEntry<T> entry = new LazyDropdownBoxEntry<>(this.getFieldNameKey(), this.getResetButtonKey(), (Supplier)null, this.isRequireRestart(), this.defaultValue, this.saveConsumer, this.selectionsSup, this.topCellElement, this.cellCreator);
        entry.setTooltipSupplier(() -> (Optional)this.tooltipSupplier.apply(entry.getValue()));
        if (this.errorSupplier != null) {
            entry.setErrorSupplier(() -> (Optional)this.errorSupplier.apply(entry.getValue()));
        }

        entry.setSuggestionMode(this.suggestionMode);
        return (LazyDropdownBoxEntry<T>) this.finishBuilding(entry);
    }

    public static LazyDropdownMenuBuilder<T> toLazy(DropdownMenuBuilder<T> builder) {
        return new LazyDropdownMenuBuilder<>(builder.getResetButtonKey(), builder.getFieldNameKey(), builder.topCellElement, builder.cellCreator);
    }
}
