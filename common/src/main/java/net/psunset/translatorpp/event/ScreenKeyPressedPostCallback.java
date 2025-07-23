package net.psunset.translatorpp.event;

import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface ScreenKeyPressedPostCallback extends TPPEvent.Callback {
    void afterKeyPress(Screen screen, int key, int scancode, int modifiers);

    static ScreenKeyPressedPostCallback merge(Iterable<ScreenKeyPressedPostCallback> callbacks) {
        return (screen, key, scancode, modifiers) -> {
            for (var callback : callbacks) {
                callback.afterKeyPress(screen, key, scancode, modifiers);
            }
        };
    }
}
