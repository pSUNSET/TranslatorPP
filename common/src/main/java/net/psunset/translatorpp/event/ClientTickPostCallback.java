package net.psunset.translatorpp.event;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ClientTickPostCallback extends TPPEvent.Callback {
    void afterTick(@Nullable Minecraft client);

    static ClientTickPostCallback merge(Iterable<ClientTickPostCallback> callbacks) {
        return client -> {
            for (var callback : callbacks) {
                callback.afterTick(client);
            }
        };
    }
}