package net.psunset.translatorpp.tool;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClientUtl {

    public static void message(Component component) {
        message(Minecraft.getInstance(), component);
    }

    public static void message(@Nullable Player player, Component component) {
        if (player != null && player.isLocalPlayer()) {
            player.sendSystemMessage(component);
        }
    }

    public static void message(@NotNull Minecraft client, Component component) {
        client.getChatListener().handleSystemMessage(component, true);
    }
}
