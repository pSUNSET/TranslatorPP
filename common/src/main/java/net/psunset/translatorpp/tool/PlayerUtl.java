package net.psunset.translatorpp.tool;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class PlayerUtl {

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in higher versions (1.21.x).
     * But this function is compatible with all 1.21.x versions.
     */
    @Environment(EnvType.CLIENT)
    public static void clientMessage(Component component) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().getChatListener().handleSystemMessage(component, false);
        }
    }

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in higher versions (1.21.x).
     * But this function is compatible with all 1.21.x versions.
     */
    @Environment(EnvType.CLIENT)
    public static void clientMessage(@Nullable Player player, Component component) {
        if (player != null && player.isLocalPlayer()) {
            Minecraft.getInstance().getChatListener().handleSystemMessage(component, false);
        }
    }

    /**
     * {@link Player#sendSystemMessage(Component)} is not defined in higher versions (1.21.x).
     * But this function is compatible with all 1.21.x versions.
     */
    public static void clientMessage(Minecraft client, Component component) {
        client.getChatListener().handleSystemMessage(component, false);
    }
}
