package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/**
 * Reels in and recasts a fishing rod when a bite is detected via the bobber's
 * downward velocity spike. Intended for single-player and private testing.
 */
public final class AutoFish extends Module {

    private long lastAction;

    public AutoFish() {
        super("Auto Fish", "Auto-reels and recasts", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (player().getMainHandStack().getItem() != Items.FISHING_ROD) {
            return;
        }
        if (System.currentTimeMillis() - lastAction < 800) {
            return;
        }
        if (player().fishHook == null) {
            useRod();
            return;
        }
        if (player().fishHook.getVelocity().y < -0.15) {
            useRod();
            // Recast shortly after reeling in.
            lastAction = System.currentTimeMillis() - 300;
        }
    }

    private void useRod() {
        mc.interactionManager.interactItem(player(), Hand.MAIN_HAND);
        player().swingHand(Hand.MAIN_HAND);
        lastAction = System.currentTimeMillis();
    }
}
