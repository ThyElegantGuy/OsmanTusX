package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import net.minecraft.client.network.PlayerListEntry;

/** Displays the local player's latency to the server. */
public final class PingHud extends TextHudModule {

    public PingHud() {
        super("Ping", "Shows network latency", 0.01, 0.08);
    }

    @Override
    protected String getText() {
        int ping = 0;
        if (mc.getNetworkHandler() != null && mc.player != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (entry != null) {
                ping = entry.getLatency();
            }
        }
        return "Ping: " + ping + "ms";
    }
}
