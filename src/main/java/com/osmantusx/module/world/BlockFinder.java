package com.osmantusx.module.world;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.module.render.Esp;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.setting.StringSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

/** Locates and highlights a configurable block type within a scan radius. */
public final class BlockFinder extends Module {

    private final StringSetting target = add(new StringSetting("Block", "Block id to find", "diamond_ore"));
    private final IntSetting radius = add(new IntSetting("Radius", "Scan radius", 12, 4, 32));

    private static final Color COLOR = new Color(120, 255, 140);

    private final List<BlockPos> found = new ArrayList<>();

    public BlockFinder() {
        super("Block Finder", "Finds a specific block", Category.WORLD);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.POST || !inGame()) {
            return;
        }
        Identifier id = Identifier.tryParse(target.get().trim().toLowerCase());
        if (id == null || !Registries.BLOCK.containsId(id)) {
            found.clear();
            return;
        }
        Block block = Registries.BLOCK.get(id);
        found.clear();
        BlockPos origin = player().getBlockPos();
        int r = radius.get();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    pos.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (world().getBlockState(pos).getBlock() == block) {
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
        for (BlockPos pos : found) {
            Box box = new Box(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
            double[] bounds = Esp.projectBox(box);
            if (bounds != null) {
                Render2DUtil.outline(context, bounds[0], bounds[1],
                        bounds[2] - bounds[0], bounds[3] - bounds[1], COLOR);
            }
        }
        Render2DUtil.text(context, "Found: " + found.size(), 4, 4, OsmanTusX.THEMES.accent());
    }

    @Override
    protected void onDisable() {
        found.clear();
    }
}
