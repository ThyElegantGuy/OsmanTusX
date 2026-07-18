package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Stops the player from walking off ledges by cancelling horizontal velocity
 * when the block they would move onto has no floor beneath it.
 */
public final class SafeWalk extends Module {

    public SafeWalk() {
        super("Safe Walk", "Prevents walking off edges", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.POST || !inGame() || !player().isOnGround()) {
            return;
        }
        Vec3d velocity = player().getVelocity();
        if (isUnsupported(player().getX() + velocity.x, player().getZ())) {
            player().setVelocity(0, velocity.y, velocity.z);
            velocity = player().getVelocity();
        }
        if (isUnsupported(player().getX(), player().getZ() + velocity.z)) {
            player().setVelocity(velocity.x, velocity.y, 0);
        }
    }

    private boolean isUnsupported(double x, double z) {
        BlockPos below = BlockPos.ofFloored(x, player().getY() - 0.5, z);
        return world().getBlockState(below).isAir();
    }
}
