package com.osmantusx.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** A bounded floating-point slider value. */
public final class DoubleSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double step;

    public DoubleSetting(String name, String description, double defaultValue, double min, double max, double step) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    @Override
    public void set(Double newValue) {
        super.set(clampAndSnap(newValue));
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getStep() {
        return step;
    }

    /** @return the current value as a float for convenient rendering math. */
    public float getFloat() {
        return get().floatValue();
    }

    /** @return the current value as an int (truncated). */
    public int getInt() {
        return get().intValue();
    }

    private double clampAndSnap(double raw) {
        double clamped = Math.max(min, Math.min(max, raw));
        if (step > 0) {
            clamped = Math.round(clamped / step) * step;
        }
        // Guard against floating point dust like 4.999999.
        return Math.round(clamped * 1000.0) / 1000.0;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsDouble());
        }
    }
}
