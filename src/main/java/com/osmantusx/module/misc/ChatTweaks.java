package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Adds timestamps to incoming chat messages. Applied by {@code ChatProcessor}
 * through {@code ChatHudMixin}.
 */
public final class ChatTweaks extends Module {

    private static boolean active;

    public ChatTweaks() {
        super("Chat Tweaks", "Adds timestamps to chat", Category.MISC);
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
