package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.hud.HudModule;
import net.minecraft.client.gui.DrawContext;

/**
 * Toggle that enables/disables the on-screen notification toasts. The actual
 * rendering is handled by {@link com.osmantusx.manager.NotificationManager};
 * this module simply exposes the feature in the HUD category.
 */
public final class NotificationsHud extends HudModule {

    public NotificationsHud() {
        super("Notifications", "Enables popup notifications", 0.0, 0.0);
    }

    @Override
    protected void onEnable() {
        OsmanTusX.NOTIFICATIONS.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        OsmanTusX.NOTIFICATIONS.setEnabled(false);
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        // Rendering handled by NotificationManager.
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 0;
    }
}
