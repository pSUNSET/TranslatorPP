package net.psunset.translatorpp.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.psunset.translatorpp.command.ClientCommandSourceStack;

@FunctionalInterface
public interface RegisterClientCommandsCallbacks extends Event.Callback {
    void register(CommandDispatcher<ClientCommandSourceStack> dispatcher, CommandBuildContext context);

    Event<RegisterClientCommandsCallbacks> EVENT = new Event<>(RegisterClientCommandsCallbacks.class, RegisterClientCommandsCallbacks::merge);

    private static RegisterClientCommandsCallbacks merge(RegisterClientCommandsCallbacks[] callbacks) {
        return (dispatcher, context) -> {
            for (var callback : callbacks) {
                callback.register(dispatcher, context);
            }
        };
    }
}
