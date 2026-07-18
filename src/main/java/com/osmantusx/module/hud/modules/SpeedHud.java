package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

/** Displays horizontal movement speed in blocks per second. */
public final class SpeedHud extends TextHudModule {

    public SpeedHud() {
        super("Speed", "Shows horizontal speed", 0.01, 0.20);
    }

    @Override
    protected String getText() {
        if (mc.player == null) {
            return "Speed: 0.0";
        }
        double dx = mc.player.getX() - mc.player.lastX;
        double dz = mc.player.getZ() - mc.player.lastZ;
        double bps = Math.sqrt(dx * dx + dz * dz) * 20.0;
        return String.format("Speed: %.1f b/s", bps);
    }
}
