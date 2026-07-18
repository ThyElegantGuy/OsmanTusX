package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;

/**
 * Enables client-side creative-style flight by toggling the player's ability
 * flags and fly speed. Designed for singleplayer/creative use.
 */
public final class Flight extends Module {

    private final DoubleSetting speed = add(new DoubleSetting("Speed", "Fly speed", 0.1, 0.05, 1.0, 0.05));

    public Flight() {
        super("Flight", "Allows free flight", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
        if (player() != null) {
            player().getAbilities().allowFlying = true;
            player().getAbilities().flying = true;
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        player().getAbilities().allowFlying = true;
        player().getAbilities().flying = true;
        player().getAbilities().setFlySpeed(speed.get().floatValue());
    }

    @Override
    protected void onDisable() {
        if (player() != null && !player().isCreative()) {
            player().getAbilities().allowFlying = false;
            player().getAbilities().flying = false;
            player().getAbilities().setFlySpeed(0.05f);
        }
    }
}
