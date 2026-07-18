package com.osmantusx.module.render;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Disables the camera tilt shown when taking damage. The effect is applied by
 * {@code GameRendererMixin}, which checks {@link #isActive()}.
 */
public final class NoHurtCam extends Module {

    private static boolean active;

    public NoHurtCam() {
        super("No Hurt Cam", "Removes damage camera tilt", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        active = true;
    }

    @Override
    protected void onDisable() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }
}
