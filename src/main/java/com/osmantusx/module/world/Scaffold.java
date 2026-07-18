package com.osmantusx.module.world;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/** Places blocks beneath the player to bridge across gaps. */
public final class Scaffold extends Module {

    public Scaffold() {
        super("Scaffold", "Places blocks below you", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (!(player().getMainHandStack().getItem() instanceof BlockItem)) {
            return;
        }
        BlockPos below = player().getBlockPos().down();
        if (!world().getBlockState(below).isAir()) {
            return;
        }
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = below.offset(dir);
            if (world().getBlockState(neighbor).isAir()) {
                continue;
            }
            Vec3d hit = Vec3d.ofCenter(neighbor).add(Vec3d.of(dir.getOpposite().getVector()).multiply(0.5));
            BlockHitResult result = new BlockHitResult(hit, dir.getOpposite(), neighbor, false);
            mc.interactionManager.interactBlock(player(), Hand.MAIN_HAND, result);
            player().swingHand(Hand.MAIN_HAND);
            break;
        }
    }
}
