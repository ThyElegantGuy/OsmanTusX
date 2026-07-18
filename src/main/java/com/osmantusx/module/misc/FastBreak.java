package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Removes the delay between block breaks. Shares the block-breaking cooldown
 * hook in {@code ClientPlayerInteractionManagerMixin}, which checks
 * {@link #isActive()}.
 */
public final class FastBreak extends Module {

    private static boolean active;

    public FastBreak() {
        super("Fast Break", "Removes block breaking delay", Category.MISC);
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
