package com.osmantusx.manager;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Module;
import com.osmantusx.util.math.MathUtil;
import com.osmantusx.util.math.Timer;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Queues transient on-screen notifications (toasts) and renders them in the
 * bottom-right corner with slide-in/out animations. Registered on the event bus
 * so it draws every HUD frame.
 */
public final class NotificationManager {

    /** Severity of a notification, controlling its accent colour. */
    public enum Type {
        INFO(new Color(80, 150, 255)),
        SUCCESS(new Color(60, 220, 120)),
        WARNING(new Color(255, 190, 60)),
        ERROR(new Color(255, 70, 70));

        private final Color accent;

        Type(Color accent) {
            this.accent = accent;
        }

        public Color accent() {
            return accent;
        }
    }

    /** A single active notification with its own lifetime timer. */
    private static final class Notification {
        final String title;
        final String message;
        final Type type;
        final long durationMs;
        final Timer timer = new Timer();

        Notification(String title, String message, Type type, long durationMs) {
            this.title = title;
            this.message = message;
            this.type = type;
            this.durationMs = durationMs;
        }
    }

    private static final long ANIMATION_MS = 250;
    private static final int MAX_VISIBLE = 6;

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final List<Notification> notifications = new ArrayList<>();

    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Pushes a titled notification with the default 3s lifetime. */
    public void push(String title, String message, Type type) {
        if (!enabled) {
            return;
        }
        notifications.add(new Notification(title, message, type, 3000));
        while (notifications.size() > 32) {
            notifications.remove(0);
        }
    }

    public void info(String message) {
        push("Info", message, Type.INFO);
    }

    public void success(String message) {
        push("Success", message, Type.SUCCESS);
    }

    public void error(String message) {
        push("Error", message, Type.ERROR);
    }

    /** Convenience helper invoked by {@link Module} on every toggle. */
    public void moduleToggled(Module module) {
        if (module.isEnabled()) {
            OsmanTusX.SOUNDS.enableSound();
        } else {
            OsmanTusX.SOUNDS.disableSound();
        }
        push(module.getName(), module.isEnabled() ? "Enabled" : "Disabled",
                module.isEnabled() ? Type.SUCCESS : Type.WARNING);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        DrawContext context = event.context();
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        double width = 150;
        double height = 26;
        double margin = 6;
        double y = screenHeight - margin - height;

        int drawn = 0;
        for (Iterator<Notification> it = notifications.iterator(); it.hasNext(); ) {
            Notification n = it.next();
            long elapsed = n.timer.elapsed();
            if (elapsed >= n.durationMs) {
                it.remove();
                continue;
            }
            if (drawn >= MAX_VISIBLE) {
                continue;
            }

            double slide = slideOffset(elapsed, n.durationMs, width);
            double x = screenWidth - width - margin + slide;

            Render2DUtil.roundedRect(context, x, y, width, height, 3, new Color(20, 22, 30, 235));
            Render2DUtil.rect(context, x, y, 3, height, n.type.accent());
            Render2DUtil.text(context, n.title, x + 8, y + 4, Color.WHITE);
            Render2DUtil.text(context, n.message, x + 8, y + 15, new Color(180, 185, 195));

            y -= height + 4;
            drawn++;
        }
    }

    /** Computes the horizontal slide offset for the intro/outro animation. */
    private double slideOffset(long elapsed, long duration, double width) {
        if (elapsed < ANIMATION_MS) {
            double t = elapsed / (double) ANIMATION_MS;
            return MathUtil.lerp(width + 10, 0, easeOut(t));
        }
        long remaining = duration - elapsed;
        if (remaining < ANIMATION_MS) {
            double t = remaining / (double) ANIMATION_MS;
            return MathUtil.lerp(width + 10, 0, easeOut(t));
        }
        return 0;
    }

    private double easeOut(double t) {
        double clamped = MathUtil.clamp(t, 0, 1);
        return 1 - Math.pow(1 - clamped, 3);
    }
}
