package net.psunset.translatorpp.mixin.forge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.psunset.translatorpp.command.ClientCommandSourceStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements ClientCommandSourceStack {

    @Unique
    @Override
    public void tpp$sendFeedback(Component message) {
        this.tpp$sendFeedback(() -> message, false);
    }

    @Unique
    @Override
    public void tpp$sendFeedback(Supplier<Component> message, boolean broadcast) {
        ((CommandSourceStack) (Object) this).sendSuccess(message, broadcast);
    }

    @Unique
    @Override
    public void tpp$sendError(Component message) {
        ((CommandSourceStack) (Object) this).sendFailure(message);
    }

    @Unique
    @Override
    public LocalPlayer tpp$getPlayer() {
        try {
            return (LocalPlayer) ((CommandSourceStack) (Object) this).getEntityOrException();
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Unique
    @Override
    public Vec3 tpp$getPosition() {
        return ((CommandSourceStack) (Object) this).getPosition();
    }

    @Unique
    @Override
    public Vec2 tpp$getRotation() {
        return ((CommandSourceStack) (Object) this).getRotation();
    }

    @Unique
    @Override
    public ClientLevel tpp$getLevel() {
        return (ClientLevel) ((CommandSourceStack) (Object) this).getUnsidedLevel();
    }
}
