package com.osmantusx.module.render;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.Set;

/**
 * Real X-Ray, the way Wurst/Meteor do it: instead of drawing ore outlines, it
 * stops non-ore blocks from rendering (via {@code BlockMixin} hooking
 * {@code Block.shouldDrawSide}), so terrain turns transparent and only ores
 * remain visible. Toggling reloads chunks so the change takes effect.
 */
public final class XRay extends Module {

    private static final Set<Block> ORES = Set.of(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS,
            Blocks.RAW_IRON_BLOCK, Blocks.RAW_GOLD_BLOCK, Blocks.RAW_COPPER_BLOCK,
            Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK,
            Blocks.IRON_BLOCK, Blocks.NETHERITE_BLOCK, Blocks.COAL_BLOCK,
            Blocks.LAPIS_BLOCK, Blocks.REDSTONE_BLOCK,
            Blocks.SPAWNER, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST);

    private static volatile boolean active;

    public XRay() {
        super("X-Ray", "See ores through terrain", Category.RENDER);
    }

    /** @return whether X-Ray is currently hiding non-ore blocks. */
    public static boolean isActive() {
        return active;
    }

    /** @return whether the given block should stay visible under X-Ray. */
    public static boolean isVisible(BlockState state) {
        return ORES.contains(state.getBlock());
    }

    @Override
    protected void onEnable() {
        active = true;
        reloadChunks();
    }

    @Override
    protected void onDisable() {
        active = false;
        reloadChunks();
    }

    private void reloadChunks() {
        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }
}
