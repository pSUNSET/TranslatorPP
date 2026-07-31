package net.psunset.translatorpp.mixin.fabric;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.psunset.translatorpp.command.ClientCommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(FabricClientCommandSource.class)
public interface FabricClientCommandSourceMixin extends ClientCommandSourceStack {

    @Unique
    @Override
    default void tpp$sendFeedback(Component message) {
        ((FabricClientCommandSource) this).sendFeedback(message);
    }

    @Unique
    @Override
    default void tpp$sendFeedback(Supplier<Component> message, boolean broadcast) {
        this.tpp$sendFeedback(message.get());
    }

    @Unique
    @Override
    default void tpp$sendError(Component message) {
        ((FabricClientCommandSource) this).sendError(message);
    }

    @Unique
    @Override
    default LocalPlayer tpp$getPlayer() {
        return ((FabricClientCommandSource) this).getPlayer();
    }

    @Unique
    @Override
    default Vec3 tpp$getPosition() {
        return ((FabricClientCommandSource) this).getPosition();
    }

    @Unique
    @Override
    default Vec2 tpp$getRotation() {
        return ((FabricClientCommandSource) this).getRotation();
    }

    @Unique
    @Override
    default ClientLevel tpp$getLevel() {
        return ((FabricClientCommandSource) this).getLevel();
    }
}
