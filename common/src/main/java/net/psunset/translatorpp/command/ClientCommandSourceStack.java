package net.psunset.translatorpp.command;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public interface ClientCommandSourceStack extends SharedSuggestionProvider {
    void tpp$sendFeedback(Component message);

    void tpp$sendFeedback(Supplier<Component> message, boolean broadcast);

    void tpp$sendError(Component message);

    LocalPlayer tpp$getPlayer();

    Vec3 tpp$getPosition();

    Vec2 tpp$getRotation();

    ClientLevel tpp$getLevel();
}
