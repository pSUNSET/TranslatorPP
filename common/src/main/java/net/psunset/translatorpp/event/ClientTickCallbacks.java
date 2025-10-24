package net.psunset.translatorpp.event;

import net.minecraft.client.Minecraft;

public interface ClientTickCallbacks {

    Event<Post> POST = new Event<>(Post.class, Post::merge);

    @FunctionalInterface
    interface Post extends Event.Callback {
        void afterTick(Minecraft client);

        private static Post merge(Post[] callbacks) {
            return client -> {
                for (var callback : callbacks) {
                    callback.afterTick(client);
                }
            };
        }
    }
}