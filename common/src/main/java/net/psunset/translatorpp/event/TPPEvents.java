package net.psunset.translatorpp.event;

public class TPPEvents {
    public static final TPPEvent<ClientTickPostCallback> CLIENT_TICK_POST = new TPPEvent<>(ClientTickPostCallback::merge);
    public static final TPPEvent<ItemTooltipCallback> ITEM_TOOLTIP = new TPPEvent<>(ItemTooltipCallback::merge);
    public static final TPPEvent<ScreenKeyPressedPostCallback> SCREEN_KEY_PRESSED_POST = new TPPEvent<>(ScreenKeyPressedPostCallback::merge);
    public static final TPPEvent<ScreenKeyReleasedPostCallback> SCREEN_KEY_RELEASED_POST = new TPPEvent<>(ScreenKeyReleasedPostCallback::merge);
    public static final TPPEvent<ScreenRemovedCallback> SCREEN_REMOVED = new TPPEvent<>(ScreenRemovedCallback::merge);
}