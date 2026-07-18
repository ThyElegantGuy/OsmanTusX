package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.util.math.Vec3d;

/** Boosts forward momentum when jumping while sprinting. */
public final class LongJump extends Module {

    private final DoubleSetting strength = add(new DoubleSetting("Strength", "Forward boost", 1.5, 1.0, 3.0, 0.1));

    public LongJump() {
        super("Long Jump", "Leap further when sprint-jumping", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().isOnGround() && mc.options.jumpKey.isPressed() && player().isSprinting()) {
            float yaw = (float) Math.toRadians(player().getYaw());
            double factor = strength.get();
            Vec3d velocity = player().getVelocity();
            player().setVelocity(-Math.sin(yaw) * factor, velocity.y + 0.42, Math.cos(yaw) * factor);
        }
    }
}
