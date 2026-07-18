package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;

/** Boosts horizontal ground speed in the current input direction. */
public final class Speed extends Module {

    private final DoubleSetting multiplier =
            add(new DoubleSetting("Multiplier", "Speed multiplier", 1.5, 1.0, 3.0, 0.1));

    public Speed() {
        super("Speed", "Increases movement speed", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        PlayerInput input = player().input.playerInput;
        if (!input.forward() && !input.backward() && !input.left() && !input.right()) {
            return;
        }
        Vec3d velocity = player().getVelocity();
        double factor = multiplier.get();
        player().setVelocity(velocity.x * factor, velocity.y, velocity.z * factor);
    }
}
