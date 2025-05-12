package net.psunset.translatorpp.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ClientUtl {

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in higher 1.21.x versions.
     * But this function is compatible with all 1.21.x versions.
     */
    @Environment(EnvType.CLIENT)
    public static void message(Component component) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().getChatListener().handleSystemMessage(component, false);
        }
    }

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in higher 1.21.x versions.
     * But this function is compatible with all 1.21.x versions.
     */
    @Environment(EnvType.CLIENT)
    public static void message(@Nullable Player player, Component component) {
        if (player != null && player.isLocalPlayer()) {
            player.displayClientMessage(component, false);
        }
    }

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in higher 1.21.x versions.
     * But this function is compatible with all 1.21.x versions.
     */
    public static void message(Minecraft client, Component component) {
        client.getChatListener().handleSystemMessage(component, false);
    }
}
