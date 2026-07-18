package com.osmantusx.module.render;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.util.ISimpleOption;

/**
 * Maximises the game's gamma so the world appears fully lit. Vanilla clamps
 * gamma to 1.0, so the value is force-set past the cap via {@link ISimpleOption}
 * (the same approach Wurst uses) and restored on disable.
 */
public final class Fullbright extends Module {

    private static final double FULL_GAMMA = 16.0;

    private double previousGamma = 1.0;

    public Fullbright() {
        super("Fullbright", "Brightens the world", Category.RENDER);
    }

    @SuppressWarnings("unchecked")
    private void setGamma(double value) {
        ((ISimpleOption<Double>) (Object) mc.options.getGamma()).osman$forceSetValue(value);
    }

    @Override
    protected void onEnable() {
        previousGamma = mc.options.getGamma().getValue();
        setGamma(FULL_GAMMA);
    }

    @Override
    protected void onDisable() {
        setGamma(previousGamma);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.POST) {
            return;
        }
        // Keep gamma pinned in case the options screen or other code resets it.
        if (mc.options.getGamma().getValue() < FULL_GAMMA) {
            setGamma(FULL_GAMMA);
        }
    }
}
