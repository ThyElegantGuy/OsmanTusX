package com.osmantusx.module.render;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/** Maximises the game's gamma so the world appears fully lit, restoring it after. */
public final class Fullbright extends Module {

    private double previousGamma = 1.0;

    public Fullbright() {
        super("Fullbright", "Brightens the world", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        previousGamma = mc.options.getGamma().getValue();
        mc.options.getGamma().setValue(15.0);
    }

    @Override
    protected void onDisable() {
        mc.options.getGamma().setValue(previousGamma);
    }
}
