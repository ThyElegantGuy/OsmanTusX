package com.osmantusx.module.render;

import com.osmantusx.util.render.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Set;

/** Highlights storage blocks (chests, barrels, shulkers, hoppers, etc.). */
public final class StorageEsp extends BlockEspModule {

    private static final Set<Block> STORAGE = Set.of(
            Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.BARREL,
            Blocks.SHULKER_BOX, Blocks.HOPPER, Blocks.DISPENSER, Blocks.DROPPER,
            Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER);

    private static final Color COLOR = new Color(255, 180, 60);

    public StorageEsp() {
        super("Storage ESP", "Highlights containers");
    }

    @Override
    protected boolean matches(Block block) {
        return STORAGE.contains(block);
    }

    @Override
    protected Color color() {
        return COLOR;
    }
}
