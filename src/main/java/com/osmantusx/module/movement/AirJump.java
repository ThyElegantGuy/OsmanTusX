package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.util.math.Vec3d;

/** Allows a single mid-air jump each time the player leaves the ground. */
public final class AirJump extends Module {

    private boolean used;

    public AirJump() {
        super("Air Jump", "Jump once while airborne", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().isOnGround()) {
            used = false;
            return;
        }
        if (!used && mc.options.jumpKey.isPressed()) {
            used = true;
            Vec3d velocity = player().getVelocity();
            player().setVelocity(velocity.x, 0.42, velocity.z);
        }
    }
}
