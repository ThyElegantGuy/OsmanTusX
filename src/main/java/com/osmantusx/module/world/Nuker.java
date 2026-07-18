package com.osmantusx.module.world;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/** Breaks blocks around the player within a configurable radius. */
public final class Nuker extends Module {

    private final DoubleSetting range = add(new DoubleSetting("Range", "Break radius", 4.0, 1.0, 6.0, 0.5));

    public Nuker() {
        super("Nuker", "Breaks nearby blocks", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        int r = (int) Math.ceil(range.get());
        BlockPos origin = player().getBlockPos();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = origin.add(dx, dy, dz);
                    if (world().getBlockState(pos).isAir()
                            || world().getBlockState(pos).getBlock() == Blocks.BEDROCK) {
                        continue;
                    }
                    double dist = pos.getSquaredDistance(player().getEntityPos());
                    if (dist <= range.get() * range.get() && dist < bestDist) {
                        bestDist = dist;
                        best = pos;
                    }
                }
            }
        }
        if (best != null) {
            mc.interactionManager.attackBlock(best, Direction.UP);
            mc.interactionManager.updateBlockBreakingProgress(best, Direction.UP);
            player().swingHand(Hand.MAIN_HAND);
        }
    }
}
