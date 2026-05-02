package net.psunset.translatorpp.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClientUtl {

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined after 1.21.1 versions.
     * But this function is compatible with all 1.21.x versions.
     */
    public static void message(Component component) {
        message(Minecraft.getInstance(), component);
    }

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in after 1.21.1 versions.
     * But this function is compatible with all 1.21.x versions.
     */
    public static void message(@Nullable Player player, Component component) {
        if (player != null && player.isLocalPlayer()) {
            player.displayClientMessage(component, false);
        }
    }

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in after 1.21.1 versions.
     * But this function is compatible with all 1.21.x versions.
     */
    public static void message(@NotNull Minecraft client, Component component) {
        client.getChatListener().handleSystemMessage(component, false);
    }
}
