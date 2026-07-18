package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

/** Automatically reconnects to the last server after a disconnect. */
public final class AutoReconnect extends Module {

    private final IntSetting delay = add(new IntSetting("Delay", "Seconds before reconnect", 3, 1, 30));

    private ServerInfo lastServer;
    private long disconnectedAt;

    public AutoReconnect() {
        super("Auto Reconnect", "Reconnects after disconnect", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE) {
            return;
        }
        if (mc.getCurrentServerEntry() != null) {
            lastServer = mc.getCurrentServerEntry();
        }
        if (mc.currentScreen instanceof DisconnectedScreen && lastServer != null) {
            if (disconnectedAt == 0) {
                disconnectedAt = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - disconnectedAt >= delay.get() * 1000L) {
                disconnectedAt = 0;
                ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), mc, buildAddress(), lastServer,
                        false, null);
            }
        } else if (!(mc.currentScreen instanceof DisconnectedScreen)) {
            disconnectedAt = 0;
        }
    }

    private ServerAddress buildAddress() {
        return ServerAddress.parse(lastServer.address);
    }
}
