package net.psunset.translatorpp.fabric.config;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class LazyDropdownBoxEntry<T> extends DropdownBoxEntry<T> {

    public LazyDropdownBoxEntry(Component fieldName, @NotNull Component resetButtonKey, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart, @Nullable Supplier<T> defaultValue, @Nullable Consumer<T> saveConsumer, Supplier<ImmutableList<T>> selectionsSup, @NotNull SelectionTopCellElement<T> topRenderer, @NotNull SelectionCellCreator<T> cellCreator) {
        super(fieldName, resetButtonKey, tooltipSupplier, requiresRestart, defaultValue, saveConsumer, null, topRenderer, cellCreator);
        this.selectionElement = new SelectionElement<>(this, new Rectangle(0, 0, 150, 20), new LazyDropdownMenuElement<>(selectionsSup), topRenderer, cellCreator);
    }

    public static class LazyDropdownMenuElement<R> extends DropdownBoxEntry.DefaultDropdownMenuElement<R> {

        protected Supplier<ImmutableList<R>> selectionsSup;

        public LazyDropdownMenuElement(@NotNull Supplier<ImmutableList<R>> selectionsSup) {
            super(ImmutableList.of());
            this.selectionsSup = selectionsSup;
        }

        @Override
        public @NotNull ImmutableList<R> getSelections() {
            return this.selectionsSup.get();
        }
    }
}
