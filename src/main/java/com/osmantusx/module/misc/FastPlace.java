package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Removes the delay between block/item placements. The cooldown is zeroed by
 * {@code MinecraftClientMixin}, which checks {@link #isActive()}.
 */
public final class FastPlace extends Module {

    private static boolean active;

    public FastPlace() {
        super("Fast Place", "Removes right-click placement delay", Category.MISC);
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
