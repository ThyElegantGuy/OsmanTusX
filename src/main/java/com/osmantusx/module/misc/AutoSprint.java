package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/** Keeps the player sprinting whenever they are moving forward. */
public final class AutoSprint extends Module {

    public AutoSprint() {
        super("Auto Sprint", "Always sprint", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().input.playerInput.forward() && !player().isSneaking() && !player().horizontalCollision) {
            player().setSprinting(true);
        }
    }
}
