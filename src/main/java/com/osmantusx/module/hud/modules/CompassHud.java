package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import com.osmantusx.util.math.MathUtil;

/** Displays the player's yaw as a compass bearing in degrees. */
public final class CompassHud extends TextHudModule {

    public CompassHud() {
        super("Compass", "Shows your bearing in degrees", 0.01, 0.17);
    }

    @Override
    protected String getText() {
        if (mc.player == null) {
            return "Bearing: -";
        }
        int bearing = Math.floorMod((int) MathUtil.wrapDegrees(mc.player.getYaw()), 360);
        return "Bearing: " + bearing + "\u00B0";
    }
}
