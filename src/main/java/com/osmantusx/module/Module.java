package com.osmantusx.module;

import com.osmantusx.OsmanTusX;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.Setting;
import com.osmantusx.util.Wrapper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for every feature in the client.
 *
 * <p>A module owns its settings and lifecycle. When enabled it registers itself
 * on the {@link com.osmantusx.event.EventBus}; any {@link com.osmantusx.event.EventHandler}
 * methods it declares then start receiving events. Disabling unregisters it.
 * Subclasses override {@link #onEnable()}/{@link #onDisable()} for setup and
 * teardown and declare event handlers for their behaviour.</p>
 */
public abstract class Module implements Wrapper {

    private final String name;
    private final String description;
    private final Category category;

    private final List<Setting<?>> settings = new ArrayList<>();

    /** Optional per-module suffix shown in the array-list HUD (e.g. mode). */
    private String tag = "";

    private int keyBind;
    private boolean enabled;
    private boolean favorite;
    /** Whether the module appears in the array-list HUD when active. */
    private boolean visible = true;

    protected Module(String name, String description, Category category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN);
    }

    protected Module(String name, String description, Category category, int keyBind) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keyBind = keyBind;
    }

    // --- Lifecycle -----------------------------------------------------------

    /** Called when the module transitions to enabled. */
    protected void onEnable() {
    }

    /** Called when the module transitions to disabled. */
    protected void onDisable() {
    }

    /** Flips the enabled state, running the appropriate lifecycle hooks. */
    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean value) {
        if (this.enabled == value) {
            return;
        }
        this.enabled = value;
        if (value) {
            OsmanTusX.EVENT_BUS.register(this);
            onEnable();
        } else {
            onDisable();
            OsmanTusX.EVENT_BUS.unregister(this);
        }
        OsmanTusX.NOTIFICATIONS.moduleToggled(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    // --- Settings ------------------------------------------------------------

    /** Registers a setting and returns it for fluent field assignment. */
    protected <T extends Setting<?>> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    /** Convenience factory for the common "enabled while in game" guard. */
    protected BooleanSetting toggleSetting(String name, String description, boolean value) {
        return add(new BooleanSetting(name, description, value));
    }

    // --- Metadata ------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getTag() {
        return tag;
    }

    protected void setTag(String tag) {
        this.tag = tag == null ? "" : tag;
    }

    /** @return the array-list display name, including the tag when present. */
    public String getDisplayName() {
        return tag.isEmpty() ? name : name + " " + tag;
    }
}
