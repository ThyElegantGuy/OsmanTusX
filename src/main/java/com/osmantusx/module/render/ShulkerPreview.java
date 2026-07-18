package com.osmantusx.module.render;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Shows the contents of a shulker box when hovering it in a container. The
 * preview is drawn by {@code HandledScreenMixin}, which checks {@link #isActive()}.
 */
public final class ShulkerPreview extends Module {

    private static boolean active;

    public ShulkerPreview() {
        super("Shulker Preview", "Preview shulker contents on hover", Category.RENDER);
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
