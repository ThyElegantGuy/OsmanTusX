package com.osmantusx.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** A GLFW key binding used to toggle modules or trigger actions. */
public final class KeybindSetting extends Setting<Integer> {

    public KeybindSetting(String name, String description, int defaultKey) {
        super(name, description, defaultKey);
    }

    /** @return {@code true} when no key is assigned. */
    public boolean isUnbound() {
        return get() == GLFW.GLFW_KEY_UNKNOWN;
    }

    /** @return a human-readable label for the currently bound key. */
    public String getKeyName() {
        if (isUnbound()) {
            return "None";
        }
        return InputUtil.Type.KEYSYM.createFromCode(get()).getLocalizedText().getString().toUpperCase();
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsInt());
        }
    }
}
