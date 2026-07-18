package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.client.gui.screen.DeathScreen;

/** Instantly respawns the player when the death screen appears. */
public final class AutoRespawn extends Module {

    public AutoRespawn() {
        super("Auto Respawn", "Respawns automatically on death", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || player() == null) {
            return;
        }
        if (mc.currentScreen instanceof DeathScreen) {
            player().requestRespawn();
            mc.setScreen(null);
        }
    }
}
