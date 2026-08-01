package net.psunset.translatorpp.mixin.wynntils;

import com.wynntils.core.components.Services;
import com.wynntils.services.chat.WrappingChatComponent;
import com.wynntils.services.chat.type.ChatTab;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.psunset.translatorpp.api.ChatComponentAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(WrappingChatComponent.class)
public abstract class WrappingChatComponentMixin extends ChatComponent implements ChatComponentAccessor {
    public WrappingChatComponentMixin(Minecraft minecraft) {
        super(minecraft);
    }

    @Unique
    @Override
    @Nullable
    public String translatorpp$getMessageContentAt(double globalMouseX, double globalMouseY) {
        ChatTab tab = Services.ChatTab.getFocusedTab();
        if (tab == null) return null;
        Optional<ChatComponent> optional = Services.ChatTab.getChatComponent(tab);
        if (optional.isEmpty()) return null;
        ChatComponent chat = optional.get();
        ChatComponentAccessor ext = (ChatComponentAccessor) chat;

        double mouseX = this.translatorpp$screenToChatX(globalMouseX);
        double mouseY = this.translatorpp$screenToChatY(globalMouseY);
        int i = this.translatorpp$getMessageLineIndexAt(mouseX, mouseY);

        if (i >= 0 && i < ext.translatorpp$getMessageIndexTrimmedToAll().length) {
            int idx = ext.translatorpp$getMessageIndexTrimmedToAll()[i];
            if (idx >= 0 && idx < chat.allMessages.size()) {
                return chat.allMessages.get(idx).content().getString();
            }
        }
        return null;
    }
}
