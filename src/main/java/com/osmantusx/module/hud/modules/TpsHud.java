package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import com.osmantusx.util.TickRateTracker;

/** Displays the estimated server tick rate. */
public final class TpsHud extends TextHudModule {

    public TpsHud() {
        super("TPS", "Shows the estimated tick rate", 0.01, 0.29);
    }

    @Override
    protected String getText() {
        return String.format("TPS: %.1f", TickRateTracker.tps());
    }
}
