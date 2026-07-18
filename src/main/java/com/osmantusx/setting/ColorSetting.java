package com.osmantusx.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.osmantusx.util.render.Color;

/** A color option edited through the ClickGUI color picker. */
public final class ColorSetting extends Setting<Color> {

    private boolean rainbow;

    public ColorSetting(String name, String description, Color defaultValue) {
        super(name, description, defaultValue);
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    @Override
    public JsonElement toJson() {
        // Persist the ARGB value and rainbow flag in a single packed string.
        return new JsonPrimitive(get().argb() + ":" + rainbow);
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element == null || !element.isJsonPrimitive()) {
            return;
        }
        String raw = element.getAsString();
        String[] parts = raw.split(":");
        try {
            set(Color.fromArgb(Integer.parseInt(parts[0])));
            if (parts.length > 1) {
                rainbow = Boolean.parseBoolean(parts[1]);
            }
        } catch (NumberFormatException ignored) {
            // Leave the default value in place on malformed data.
        }
    }
}
