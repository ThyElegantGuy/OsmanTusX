package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/** Keeps the player sprinting whenever there is forward movement input. */
public final class Sprint extends Module {

    public Sprint() {
        super("Sprint", "Always sprint when moving", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().input.playerInput.forward() && !player().isSneaking()) {
            player().setSprinting(true);
        }
    }
}
