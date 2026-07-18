package com.osmantusx.module.render;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.client.render.fog.FogRenderer;

/**
 * Removes distance fog using the game's built-in fog toggle, restoring it when
 * disabled.
 */
public final class NoFog extends Module {

    public NoFog() {
        super("No Fog", "Disables distance fog", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        // toggleFog() returns the new enabled state; ensure fog ends up off.
        if (FogRenderer.toggleFog()) {
            FogRenderer.toggleFog();
        }
    }

    @Override
    protected void onDisable() {
        // Ensure fog ends up back on.
        if (!FogRenderer.toggleFog()) {
            FogRenderer.toggleFog();
        }
    }
}
