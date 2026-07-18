package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Improves the chat by marking duplicate consecutive messages. Applied by
 * {@code ChatProcessor} through {@code ChatHudMixin}.
 */
public final class BetterChat extends Module {

    private static boolean active;

    public BetterChat() {
        super("Better Chat", "Marks duplicate messages", Category.MISC);
    }

    @Override
    protected void onEnable() {
        active = true;
    }

    @Override
    protected void onDisable() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }
}
