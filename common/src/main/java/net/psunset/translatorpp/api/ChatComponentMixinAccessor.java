package net.psunset.translatorpp.api;

import net.psunset.translatorpp.mixin.ChatComponentMixin;
import org.jetbrains.annotations.Nullable;

/**
 * To access mixin methods in ChatComponent.
 */
public interface ChatComponentMixinAccessor {
    /**
     * @see ChatComponentMixin#translatorpp$getMessageContentAt(double, double)
     */
    @Nullable String translatorpp$getMessageContentAt(double x, double y);
}
