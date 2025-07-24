package net.psunset.translatorpp.event;

import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface ScreenKeyReleasedPostCallback extends TPPEvent.Callback {
    void afterKeyRelease(Screen screen, int key, int scancode, int modifiers);

    static ScreenKeyReleasedPostCallback merge(Iterable<ScreenKeyReleasedPostCallback> callbacks) {
        return (screen, key, scancode, modifiers) -> {
            for (var callback : callbacks) {
                callback.afterKeyRelease(screen, key, scancode, modifiers);
            }
        };
    }
}