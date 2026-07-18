package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

/** Displays the current frames-per-second. */
public final class FpsHud extends TextHudModule {

    public FpsHud() {
        super("FPS", "Shows the current framerate", 0.01, 0.02);
    }

    @Override
    protected String getText() {
        return "FPS: " + mc.getCurrentFps();
    }
}
