package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

/** Displays the player's block coordinates. */
public final class CoordinatesHud extends TextHudModule {

    public CoordinatesHud() {
        super("Coordinates", "Shows your position", 0.01, 0.11);
    }

    @Override
    protected String getText() {
        if (mc.player == null) {
            return "XYZ: -";
        }
        return String.format("XYZ: %.0f, %.0f, %.0f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ());
    }
}
