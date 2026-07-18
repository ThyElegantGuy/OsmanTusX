package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/**
 * Counteracts the movement slowdown applied while using items by re-asserting
 * the sprint state each tick.
 */
public final class NoSlow extends Module {

    public NoSlow() {
        super("No Slow", "Removes item-use slowdown", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().isUsingItem() && player().input.playerInput.forward()) {
            player().setSprinting(true);
        }
    }
}
