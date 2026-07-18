package com.osmantusx.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** A free-form text option (chat messages, watermark text, etc.). */
public final class StringSetting extends Setting<String> {

    public StringSetting(String name, String description, String defaultValue) {
        super(name, description, defaultValue);
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(get());
    }

    @Override
    public void fromJson(JsonElement element) {
        if (element != null && element.isJsonPrimitive()) {
            set(element.getAsString());
        }
    }
}
