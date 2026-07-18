package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

/** Plays the note block the player is looking at on a steady interval. */
public final class NoteBot extends Module {

    private final IntSetting interval = add(new IntSetting("Interval", "Milliseconds between notes", 500, 100, 2000));

    private long lastPlay;

    public NoteBot() {
        super("Note Bot", "Plays note blocks rhythmically", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (System.currentTimeMillis() - lastPlay < interval.get()) {
            return;
        }
        if (!(mc.crosshairTarget instanceof BlockHitResult hit)) {
            return;
        }
        if (world().getBlockState(hit.getBlockPos()).getBlock() != Blocks.NOTE_BLOCK) {
            return;
        }
        mc.interactionManager.interactBlock(player(), Hand.MAIN_HAND, hit);
        player().swingHand(Hand.MAIN_HAND);
        lastPlay = System.currentTimeMillis();
    }
}
