package com.osmantusx.module.render;

import com.osmantusx.util.render.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Set;

/**
 * Highlights ore blocks through an ESP overlay. This projection-based highlight
 * avoids the fragile chunk-rebuild hacks of shader X-ray while still surfacing
 * valuable ores.
 */
public final class XRay extends BlockEspModule {

    private static final Set<Block> ORES = Set.of(
            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
            Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS);

    private static final Color COLOR = new Color(80, 230, 240);

    public XRay() {
        super("X-Ray", "Highlights ores");
    }

    @Override
    protected boolean matches(Block block) {
        return ORES.contains(block);
    }

    @Override
    protected Color color() {
        return COLOR;
    }
}
