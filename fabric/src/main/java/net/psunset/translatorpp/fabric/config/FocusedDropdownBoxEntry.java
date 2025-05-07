package net.psunset.translatorpp.fabric.config;

import com.google.common.collect.ImmutableList;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.math.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class FocusedDropdownBoxEntry<T> extends DropdownBoxEntry<T> {

    public FocusedDropdownBoxEntry(Component fieldName, @NotNull Component resetButtonKey, @Nullable Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart, @Nullable Supplier<T> defaultValue, @Nullable Consumer<T> saveConsumer, Supplier<ImmutableList<T>> selectionsSup, @NotNull SelectionTopCellElement<T> topRenderer, @NotNull SelectionCellCreator<T> cellCreator) {
        super(fieldName, resetButtonKey, tooltipSupplier, requiresRestart, defaultValue, saveConsumer, null, topRenderer, cellCreator);
        this.selectionElement = new SelectionElement<>(this, new Rectangle(0, 0, 150, 20), new FocusedDropdownMenuElement<>(selectionsSup), topRenderer, cellCreator);
    }

    public static class FocusedDropdownMenuElement<R> extends DropdownBoxEntry.DefaultDropdownMenuElement<R> {

        protected Supplier<ImmutableList<R>> selectionsSup;

        public FocusedDropdownMenuElement(@NotNull Supplier<ImmutableList<R>> selectionsSup) {
            super(ImmutableList.of());
            this.selectionsSup = selectionsSup;
        }

        public void refreshCells() {
            this.cells.clear();
            for (R selection : this.getSelections()) {
                this.cells.add(this.getCellCreator().create(selection));
            }
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, Rectangle rectangle, float delta) {
            refreshCells();
            super.render(graphics, mouseX, mouseY, rectangle, delta);
        }

        @Override
        public @NotNull ImmutableList<R> getSelections() {
            return this.selectionsSup.get();
        }
    }
}
