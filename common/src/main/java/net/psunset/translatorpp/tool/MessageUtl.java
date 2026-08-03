package net.psunset.translatorpp.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class MessageUtl {

    /**
     * Sends {@code message} to client side.
     */
    public static void toLocal(Component message) {
        toLocal(Minecraft.getInstance(), message);
    }

    /**
     * Sends {@code message} to client side.
     */
    public static void toLocal(Player player, Component message) {
        player.sendSystemMessage(message);
    }

    /**
     * Sends {@code message} to client side.
     */
    public static void toLocal(Minecraft client, Component message) {
        client.gui.chatListener().handleSystemMessage(message, true);
    }

    /**
     * Sends {@code message} to client side in client thread.
     */
    public static void threadSafeToLocal(Component message) {
        threadSafeToLocal(Minecraft.getInstance(), message);
    }

    /**
     * Sends {@code message} to client side in client thread.
     */
    public static void threadSafeToLocal(Minecraft client, Component message) {
        client.execute(() -> client.gui.chatListener().handleSystemMessage(message, true));
    }

    /**
     * Makes client send {@code message} to server side.
     */
    public static void toRemote(String message) {
        toRemote(Minecraft.getInstance(), message);
    }

    /**
     * Makes {@code player} send {@code message} to server side.
     */
    public static void toRemote(LocalPlayer player, String message) {
        player.connection.sendChat(message);
    }

    /**
     * Makes client send {@code message} to server side.
     */
    public static void toRemote(Minecraft client, String message) {
        client.getConnection().sendChat(message);
    }
}
