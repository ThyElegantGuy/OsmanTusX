package com.osmantusx.module.render;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared base for block highlighters. Scanning happens once per tick within a
 * cubic radius and the matched positions are cached, so the per-frame render
 * pass only projects and draws – keeping the FPS cost low.
 */
public abstract class BlockEspModule extends Module {

    protected final IntSetting radius = add(new IntSetting("Radius", "Scan radius", 8, 4, 16));

    private final List<BlockPos> found = new ArrayList<>();

    protected BlockEspModule(String name, String description) {
        super(name, description, Category.RENDER);
    }

    /** @return whether the given block should be highlighted. */
    protected abstract boolean matches(Block block);

    /** @return the outline colour for a matched block. */
    protected abstract Color color();

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.POST || !inGame()) {
            return;
        }
        found.clear();
        BlockPos origin = player().getBlockPos();
        int r = radius.get();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (matches(world().getBlockState(pos).getBlock())) {
                        found.add(pos.toImmutable());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        Color color = color();
        for (BlockPos pos : found) {
            Box box = new Box(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
            double[] bounds = Render3DUtil.projectBox(box);
            if (bounds != null) {
                Render2DUtil.outline(context, bounds[0], bounds[1],
                        bounds[2] - bounds[0], bounds[3] - bounds[1], color);
            }
        }
    }

    @Override
    protected void onDisable() {
        found.clear();
    }
}
