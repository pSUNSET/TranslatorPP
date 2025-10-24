package net.psunset.translatorpp.event;

import net.minecraft.client.gui.screens.Screen;

public interface ScreenCallbacks {

    Event<KeyPressed.Post> KEY_PRESSED_POST = new Event<>(KeyPressed.Post.class, KeyPressed.Post::merge);
    Event<KeyReleased.Post> KEY_RELEASED_POST = new Event<>(KeyReleased.Post.class, KeyReleased.Post::merge);
    Event<Removed> REMOVED = new Event<>(Removed.class, Removed::merge);

    interface KeyPressed {
        @FunctionalInterface
        interface Post extends Event.Callback {
            void afterKeyPress(Screen screen, int key, int scancode, int modifiers);

            static Post merge(Post[] callbacks) {
                return (screen, key, scancode, modifiers) -> {
                    for (var callback : callbacks) {
                        callback.afterKeyPress(screen, key, scancode, modifiers);
                    }
                };
            }
        }
    }

    interface KeyReleased {
        @FunctionalInterface
        interface Post extends Event.Callback {
            void afterKeyRelease(Screen screen, int key, int scancode, int modifiers);

            static Post merge(Post[] callbacks) {
                return (screen, key, scancode, modifiers) -> {
                    for (var callback : callbacks) {
                        callback.afterKeyRelease(screen, key, scancode, modifiers);
                    }
                };
            }
        }
    }

    @FunctionalInterface
    interface Removed extends Event.Callback {
        void onRemove(Screen screen);

        static Removed merge(Removed[] callbacks) {
            return (screen) -> {
                for (var callback : callbacks) {
                    callback.onRemove(screen);
                }
            };
        }
    }
}