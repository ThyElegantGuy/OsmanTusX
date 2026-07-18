package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.StringSetting;
import net.minecraft.client.MinecraftClient;

/**
 * Replaces the local player's name with an alias in incoming chat, applied by
 * {@code ChatProcessor} through {@code ChatHudMixin}.
 */
public final class NameProtect extends Module {

    private static NameProtect instance;

    private final StringSetting alias = add(new StringSetting("Alias", "Replacement name", "You"));

    public NameProtect() {
        super("Name Protect", "Hides your name in chat", Category.MISC);
        instance = this;
    }

    public static boolean isActive() {
        return instance != null && instance.isEnabled();
    }

    public static String apply(String message) {
        String realName = MinecraftClient.getInstance().getSession().getUsername();
        if (realName == null || realName.isEmpty() || instance == null) {
            return message;
        }
        return message.replace(realName, instance.alias.get());
    }
}
