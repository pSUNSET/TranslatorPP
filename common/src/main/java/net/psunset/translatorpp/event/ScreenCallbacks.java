package net.psunset.translatorpp.event;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;

public interface ScreenCallbacks {

    Event<KeyPressed.Post> KEY_PRESSED_POST = new Event<>(KeyPressed.Post.class, KeyPressed.Post::merge);
    Event<KeyReleased.Post> KEY_RELEASED_POST = new Event<>(KeyReleased.Post.class, KeyReleased.Post::merge);
    Event<Removed> REMOVED = new Event<>(Removed.class, Removed::merge);

    interface KeyPressed {
        @FunctionalInterface
        interface Post extends Event.Callback {
            void afterKeyPress(Screen screen, KeyEvent context);

            static Post merge(Post[] callbacks) {
                return (screen, context) -> {
                    for (var callback : callbacks) {
                        callback.afterKeyPress(screen, context);
                    }
                };
            }
        }
    }

    interface KeyReleased {
        @FunctionalInterface
        interface Post extends Event.Callback {
            void afterKeyRelease(Screen screen, KeyEvent context);

            static Post merge(Post[] callbacks) {
                return (screen, context) -> {
                    for (var callback : callbacks) {
                        callback.afterKeyRelease(screen, context);
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
