package com.osmantusx.util;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;

/**
 * Estimates the tick rate from the interval between client ticks.
 *
 * <p>A precise server TPS requires parsing time-sync packets; this client-side
 * estimate is smoothed and clamped to 20 and is accurate enough for a HUD
 * readout. Registered on the event bus during startup.</p>
 */
public final class TickRateTracker {

    private long lastTick = System.currentTimeMillis();
    private double smoothedTps = 20.0;

    private static double value = 20.0;

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.POST) {
            return;
        }
        long now = System.currentTimeMillis();
        long delta = now - lastTick;
        lastTick = now;
        if (delta <= 0) {
            return;
        }
        double instant = Math.min(20.0, 1000.0 / delta);
        smoothedTps = smoothedTps * 0.9 + instant * 0.1;
        value = smoothedTps;
    }

    /** @return the smoothed tick-rate estimate. */
    public static double tps() {
        return value;
    }
}
