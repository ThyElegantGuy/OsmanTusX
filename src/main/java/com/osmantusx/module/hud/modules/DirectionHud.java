package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import com.osmantusx.util.math.MathUtil;

/** Displays the cardinal direction the player is facing. */
public final class DirectionHud extends TextHudModule {

    private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public DirectionHud() {
        super("Direction", "Shows your facing direction", 0.01, 0.14);
    }

    @Override
    protected String getText() {
        if (mc.player == null) {
            return "Facing: -";
        }
        float yaw = MathUtil.wrapDegrees(mc.player.getYaw()) + 180.0f;
        int index = Math.round(yaw / 45.0f) & 7;
        return "Facing: " + DIRECTIONS[index];
    }
}
