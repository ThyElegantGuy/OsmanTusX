package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import com.osmantusx.util.ClickTracker;

/** Displays left/right clicks-per-second. */
public final class CpsHud extends TextHudModule {

    public CpsHud() {
        super("CPS", "Shows clicks per second", 0.01, 0.05);
    }

    @Override
    protected String getText() {
        return "CPS: " + ClickTracker.leftCps() + " | " + ClickTracker.rightCps();
    }
}
