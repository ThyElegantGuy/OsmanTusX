package com.osmantusx.setting;

import com.google.gson.JsonElement;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Base class for a configurable module option.
 *
 * <p>Every setting knows how to serialize itself to and from JSON so the
 * {@link com.osmantusx.manager.ConfigManager} can persist configs generically.
 * Settings also support a visibility predicate to build dependent/expandable
 * option trees in the ClickGUI.</p>
 *
 * @param <T> the value type held by this setting
 */
public abstract class Setting<T> {

    private final String name;
    private final String description;
    private final T defaultValue;
    private T value;

    private Supplier<Boolean> visibility = () -> true;
    private Consumer<T> onChange = ignored -> {
    };

    protected Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (newValue == null || newValue.equals(this.value)) {
            return;
        }
        this.value = newValue;
        this.onChange.accept(newValue);
    }

    public T getDefault() {
        return defaultValue;
    }

    public void reset() {
        set(defaultValue);
    }

    /** Restricts when this setting is shown in the GUI. */
    public Setting<T> visibleWhen(Supplier<Boolean> predicate) {
        this.visibility = predicate;
        return this;
    }

    public boolean isVisible() {
        return visibility.get();
    }

    /** Registers a callback fired whenever the value changes. */
    public Setting<T> onChange(Consumer<T> callback) {
        this.onChange = callback;
        return this;
    }

    /** Serializes the current value for config persistence. */
    public abstract JsonElement toJson();

    /** Restores the value from a previously serialized element. */
    public abstract void fromJson(JsonElement element);
}
