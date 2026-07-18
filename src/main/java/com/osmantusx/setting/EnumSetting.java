package com.osmantusx.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * A multiple-choice dropdown backed by a Java enum.
 *
 * @param <E> the enum type providing the available modes
 */
public final class EnumSetting<E extends Enum<E>> extends Setting<E> {

    private final E[] values;

    public EnumSetting(String name, String description, E defaultValue) {
        super(name, description, defaultValue);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }

    /** Advances to the next mode, wrapping around at the end. */
    public void cycle() {
        int next = (get().ordinal() + 1) % values.length;
        set(values[next]);
    }

    public E[] getValues() {
        return values;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get().name());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }
        String name = element.getAsString();
        for (E value : values) {
            if (value.name().equals(name)) {
                set(value);
                return;
            }
        }
    }
}
