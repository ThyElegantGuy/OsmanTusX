package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/** Periodically jumps and rotates so the player is not flagged as AFK. */
public final class AntiAfk extends Module {

    private int ticks;

    public AntiAfk() {
        super("Anti AFK", "Prevents AFK kicks", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (++ticks % 60 == 0) {
            player().setYaw(player().getYaw() + 30f);
            if (player().isOnGround()) {
                player().jump();
            }
        }
    }
}
