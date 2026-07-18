package com.osmantusx.util;

import com.osmantusx.OsmanTusX;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/** Helpers for sending client-side chat feedback and server chat/commands. */
public final class ChatUtil {

    private ChatUtil() {
    }

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final String PREFIX = "\u00A78[\u00A7b" + OsmanTusX.NAME + "\u00A78] \u00A7r";

    /** Prints a client-only message prefixed with the client name. */
    public static void info(String message) {
        if (MC.player != null) {
            MC.player.sendMessage(Text.literal(PREFIX + message), false);
        }
    }

    /** Sends a real chat message to the server (as if typed). */
    public static void sendChat(String message) {
        if (MC.player != null && MC.getNetworkHandler() != null) {
            MC.getNetworkHandler().sendChatMessage(message);
        }
    }

    /** Sends a slash command to the server (without the leading slash). */
    public static void sendCommand(String command) {
        if (MC.player != null && MC.getNetworkHandler() != null) {
            MC.getNetworkHandler().sendChatCommand(command);
        }
    }
}
