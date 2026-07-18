package com.osmantusx.module.world;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Removes the client-side cooldown between block breaks. The delay is zeroed by
 * {@code ClientPlayerInteractionManagerMixin}, which checks {@link #isActive()}.
 */
public final class FastMine extends Module {

    private static boolean active;

    public FastMine() {
        super("Fast Mine", "Removes block breaking delay", Category.WORLD);
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
