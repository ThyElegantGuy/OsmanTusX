package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;

/** Holds the forward key so the player keeps walking hands-free. */
public final class AutoWalk extends Module {

    public AutoWalk() {
        super("Auto Walk", "Walks forward automatically", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.currentScreen != null) {
            return;
        }
        mc.options.forwardKey.setPressed(true);
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.forwardKey.setPressed(false);
        }
    }
}
