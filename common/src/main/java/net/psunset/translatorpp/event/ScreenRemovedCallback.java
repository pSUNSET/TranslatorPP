package net.psunset.translatorpp.event;

import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface ScreenRemovedCallback extends TPPEvent.Callback {
    void onRemove(Screen screen);

    static ScreenRemovedCallback merge(Iterable<ScreenRemovedCallback> callbacks) {
        return screen -> {
            for (var callback : callbacks) {
                callback.onRemove(screen);
            }
        };
    }
}
