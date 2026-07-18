package com.osmantusx.util;

import com.osmantusx.module.misc.BetterChat;
import com.osmantusx.module.misc.ChatTweaks;
import com.osmantusx.module.misc.NameProtect;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Central transform applied to incoming chat lines by {@code ChatHudMixin}.
 * Each chat-oriented module contributes an optional rewrite. Formatting is
 * simplified to plain text, which keeps the transform portable.
 */
public final class ChatProcessor {

    private ChatProcessor() {
    }

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    private static String lastMessage = "";

    public static String process(String message) {
        String result = message;
        if (NameProtect.isActive()) {
            result = NameProtect.apply(result);
        }
        if (BetterChat.isActive()) {
            if (result.equals(lastMessage)) {
                result = result + " \u00A78(dupe)";
            }
        }
        lastMessage = message;
        if (ChatTweaks.isActive()) {
            result = "\u00A78[" + LocalTime.now().format(TIME) + "] \u00A7r" + result;
        }
        return result;
    }
}
