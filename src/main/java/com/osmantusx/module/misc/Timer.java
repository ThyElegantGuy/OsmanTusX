package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;

/**
 * Alters the game's tick speed. The multiplier is applied by
 * {@code RenderTickCounterMixin} via {@link #multiplier()}.
 */
public final class Timer extends Module {

    private static Timer instance;

    private final DoubleSetting speed = add(new DoubleSetting("Speed", "Tick multiplier", 1.5, 0.1, 5.0, 0.1));

    public Timer() {
        super("Timer", "Changes game speed", Category.MISC);
        instance = this;
    }

    /** @return the active multiplier, or 1.0 when disabled. */
    public static float multiplier() {
        return instance != null && instance.isEnabled() ? instance.speed.get().floatValue() : 1.0f;
    }
}
