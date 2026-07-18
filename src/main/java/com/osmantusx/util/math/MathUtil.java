package com.osmantusx.util.math;

/** Small numeric helpers used throughout rendering and module logic. */
public final class MathUtil {

    private MathUtil() {
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double lerp(double from, double to, double t) {
        return from + (to - from) * clamp(t, 0, 1);
    }

    /** Frame-rate independent smoothing towards a target. */
    public static double approach(double current, double target, double speed) {
        return current + (target - current) * clamp(speed, 0, 1);
    }

    /** Wraps a degree value into the [-180, 180) range. */
    public static float wrapDegrees(float degrees) {
        float wrapped = degrees % 360.0f;
        if (wrapped >= 180.0f) {
            wrapped -= 360.0f;
        }
        if (wrapped < -180.0f) {
            wrapped += 360.0f;
        }
        return wrapped;
    }
}
