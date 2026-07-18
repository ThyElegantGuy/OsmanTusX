package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.util.Hand;

/** Speeds up item usage by triggering the use action every tick while held. */
public final class FastUse extends Module {

    public FastUse() {
        super("Fast Use", "Removes item use delay", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (mc.options.useKey.isPressed() && !player().getMainHandStack().isEmpty()) {
            mc.interactionManager.interactItem(player(), Hand.MAIN_HAND);
        }
    }
}
