package com.osmantusx.module.render;

import com.osmantusx.util.render.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Set;

/** Highlights notable blocks such as spawners, portal frames and beacons. */
public final class BlockEsp extends BlockEspModule {

    private static final Set<Block> NOTABLE = Set.of(
            Blocks.SPAWNER, Blocks.END_PORTAL_FRAME, Blocks.BEACON,
            Blocks.BUDDING_AMETHYST);

    private static final Color COLOR = new Color(190, 120, 255);

    public BlockEsp() {
        super("Block ESP", "Highlights notable blocks");
    }

    @Override
    protected boolean matches(Block block) {
        return NOTABLE.contains(block);
    }

    @Override
    protected Color color() {
        return COLOR;
    }
}
