package com.osmantusx.util.render;

/**
 * Immutable RGBA color with convenience helpers for GUI/HUD rendering.
 *
 * <p>Components are stored as 0-255 integers. The class is intentionally small
 * and allocation-friendly since colors are created frequently while rendering.</p>
 */
public final class Color {

    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 60, 60);
    public static final Color GREEN = new Color(60, 220, 120);
    public static final Color BLUE = new Color(80, 150, 255);

    private final int r;
    private final int g;
    private final int b;
    private final int a;

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }

    /** Builds a color from a packed 0xAARRGGBB integer. */
    public static Color fromArgb(int argb) {
        return new Color(
                (argb >> 16) & 0xFF,
                (argb >> 8) & 0xFF,
                argb & 0xFF,
                (argb >> 24) & 0xFF);
    }

    /** Builds a color from HSB values (hue/sat/brightness in 0..1) and alpha 0..255. */
    public static Color fromHsb(float hue, float saturation, float brightness, int alpha) {
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, alpha);
    }

    public int r() {
        return r;
    }

    public int g() {
        return g;
    }

    public int b() {
        return b;
    }

    public int a() {
        return a;
    }

    /** @return this color packed as 0xAARRGGBB, the format Minecraft expects. */
    public int argb() {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** @return a copy of this color with the given alpha (0..255). */
    public Color withAlpha(int alpha) {
        return new Color(r, g, b, alpha);
    }

    /** @return a copy of this color scaled in brightness by {@code factor}. */
    public Color scaleBrightness(float factor) {
        return new Color(Math.round(r * factor), Math.round(g * factor), Math.round(b * factor), a);
    }

    /** Linear interpolation between this color and {@code other} by {@code t} in 0..1. */
    public Color lerp(Color other, float t) {
        float clamped = Math.max(0f, Math.min(1f, t));
        return new Color(
                Math.round(r + (other.r - r) * clamped),
                Math.round(g + (other.g - g) * clamped),
                Math.round(b + (other.b - b) * clamped),
                Math.round(a + (other.a - a) * clamped));
    }

    /** Returns the HSB representation as {hue, saturation, brightness}. */
    public float[] toHsb() {
        return java.awt.Color.RGBtoHSB(r, g, b, null);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    @Override
    public String toString() {
        return "Color[" + r + ", " + g + ", " + b + ", " + a + "]";
    }
}
