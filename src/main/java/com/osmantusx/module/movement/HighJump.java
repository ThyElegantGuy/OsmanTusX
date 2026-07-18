package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.util.math.Vec3d;

/** Adds extra upward velocity to jumps. */
public final class HighJump extends Module {

    private final DoubleSetting boost = add(new DoubleSetting("Boost", "Extra jump velocity", 0.3, 0.1, 1.0, 0.05));

    public HighJump() {
        super("High Jump", "Jump higher than normal", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().isOnGround() && mc.options.jumpKey.isPressed()) {
            Vec3d velocity = player().getVelocity();
            player().setVelocity(velocity.x, velocity.y + boost.get(), velocity.z);
        }
    }
}
