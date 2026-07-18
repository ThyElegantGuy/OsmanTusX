package com.osmantusx.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Helper methods for 2D GUI/HUD drawing built on top of {@link DrawContext}.
 *
 * <p>Only stable {@code DrawContext} primitives are used so the client stays
 * resilient across Minecraft point releases. Rounded corners are approximated
 * with a small ladder of filled rectangles, which avoids fragile low-level
 * buffer/shader APIs while still looking smooth at typical GUI scales.</p>
 */
public final class Render2DUtil {

    private Render2DUtil() {
    }

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static TextRenderer font() {
        return MC.textRenderer;
    }

    /** Wraps a string in the vanilla bold style for slightly bolder glyphs. */
    private static Text bold(String text) {
        return Text.literal(text).styled(style -> style.withBold(true));
    }

    public static int textWidth(String text) {
        return MC.textRenderer.getWidth(bold(text));
    }

    public static int textHeight() {
        return MC.textRenderer.fontHeight;
    }

    /** Filled axis-aligned rectangle at (x, y) with the given size. */
    public static void rect(DrawContext context, double x, double y, double width, double height, Color color) {
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), color.argb());
    }

    /** Two-color vertical gradient rectangle. */
    public static void gradientRect(DrawContext context, double x, double y, double width, double height,
                                    Color top, Color bottom) {
        context.fillGradient((int) x, (int) y, (int) (x + width), (int) (y + height), top.argb(), bottom.argb());
    }

    /** 1px outline around the given rectangle. */
    public static void outline(DrawContext context, double x, double y, double width, double height, Color color) {
        outline(context, x, y, width, height, color, 1);
    }

    /** Outline around the given rectangle with a configurable border thickness. */
    public static void outline(DrawContext context, double x, double y, double width, double height, Color color,
                               double thickness) {
        int t = (int) Math.max(1, Math.round(thickness));
        rect(context, x, y, width, t, color);
        rect(context, x, y + height - t, width, t, color);
        rect(context, x, y, t, height, color);
        rect(context, x + width - t, y, t, height, color);
    }

    /**
     * Filled rectangle with rounded corners approximated by a corner ladder.
     *
     * <p>All coordinates are snapped to whole pixels and each corner row uses the
     * same integer inset on the left and right, so every corner of the rectangle
     * is rounded identically (no lopsided 3px-vs-2px corners).</p>
     */
    public static void roundedRect(DrawContext context, double x, double y, double width, double height,
                                   double radius, Color color) {
        int xi = (int) Math.round(x);
        int yi = (int) Math.round(y);
        int w = (int) Math.round(width);
        int h = (int) Math.round(height);
        int r = (int) Math.max(0, Math.min(radius, Math.min(w, h) / 2.0));
        if (r <= 0) {
            rect(context, xi, yi, w, h, color);
            return;
        }
        // Central cross that leaves the four corners empty.
        rect(context, xi + r, yi, w - 2 * r, h, color);
        rect(context, xi, yi + r, r, h - 2 * r, color);
        rect(context, xi + w - r, yi + r, r, h - 2 * r, color);
        // Corner arcs, symmetric integer insets applied to both sides.
        for (int i = 0; i < r; i++) {
            double dy = i + 0.5;
            double dx = Math.sqrt(Math.max(0, (double) r * r - (r - dy) * (r - dy)));
            int inset = (int) Math.round(r - dx);
            rect(context, xi + inset, yi + i, w - 2 * inset, 1, color);
            rect(context, xi + inset, yi + h - i - 1, w - 2 * inset, 1, color);
        }
    }

    /**
     * Draws a translucent darkened backdrop behind GUIs. Real Gaussian blur
     * needs a post-processing shader; this documented approximation keeps the
     * client portable while still separating the GUI from the world.
     */
    public static void blurBackdrop(DrawContext context, int screenWidth, int screenHeight, int alpha) {
        rect(context, 0, 0, screenWidth, screenHeight, new Color(10, 12, 18, alpha));
    }

    /** Draws a smooth line using the default thickness. */
    public static void line(DrawContext context, double x1, double y1, double x2, double y2, Color color) {
        line(context, x1, y1, x2, y2, color, 1.6);
    }

    /**
     * Draws a straight line as a single rotated quad.
     *
     * <p>The old implementation stamped one tiny filled square per pixel along
     * the segment, which both looked jagged and flooded the GUI buffer with
     * thousands of quads per frame (collapsing FPS when many tracers were on).
     * Rotating one quad through the {@link DrawContext} matrix stack yields a
     * clean, evenly-weighted line at a constant, negligible cost.</p>
     */
    public static void line(DrawContext context, double x1, double y1, double x2, double y2, Color color,
                            double thickness) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length < 1.0e-3) {
            return;
        }
        int t = (int) Math.max(1, Math.round(thickness));
        int argb = color.argb();
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate((float) x1, (float) y1);
        matrices.rotate((float) Math.atan2(dy, dx));
        context.fill(0, -t / 2, (int) Math.round(length), -t / 2 + t, argb);
        matrices.popMatrix();
    }

    public static void text(DrawContext context, String text, double x, double y, Color color) {
        context.drawText(MC.textRenderer, bold(text), (int) x, (int) y, color.argb(), true);
    }

    /** Draws ordinary (non-bold) text with a shadow, a touch lighter than {@link #text}. */
    public static void regularText(DrawContext context, String text, double x, double y, Color color) {
        context.drawText(MC.textRenderer, text, (int) x, (int) y, color.argb(), true);
    }

    /** Interpolates a colour at position {@code t} in [0,1] across the given stops. */
    public static Color gradient(double t, Color... stops) {
        if (stops.length == 0) {
            return Color.WHITE;
        }
        if (stops.length == 1) {
            return stops[0];
        }
        t = Math.max(0, Math.min(1, t));
        double scaled = t * (stops.length - 1);
        int i = (int) Math.floor(scaled);
        if (i >= stops.length - 1) {
            return stops[stops.length - 1];
        }
        double f = scaled - i;
        Color a = stops[i];
        Color b = stops[i + 1];
        return new Color(
                (int) Math.round(a.r() + (b.r() - a.r()) * f),
                (int) Math.round(a.g() + (b.g() - a.g()) * f),
                (int) Math.round(a.b() + (b.b() - a.b()) * f),
                (int) Math.round(a.a() + (b.a() - a.a()) * f));
    }

    public static void textNoShadow(DrawContext context, String text, double x, double y, Color color) {
        context.drawText(MC.textRenderer, text, (int) x, (int) y, color.argb(), false);
    }

    /** Draws text centred horizontally around {@code centerX}. */
    public static void centeredText(DrawContext context, String text, double centerX, double y, Color color) {
        text(context, text, centerX - textWidth(text) / 2.0, y, color);
    }
}
