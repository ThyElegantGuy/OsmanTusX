package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Hand;

/** Eats food from the hotbar automatically when hunger drops too low. */
public final class AutoEat extends Module {

    private final IntSetting minHunger = add(new IntSetting("Min Hunger", "Eat below this", 16, 1, 19));

    private int previousSlot = -1;
    private boolean eating;

    public AutoEat() {
        super("Auto Eat", "Auto-eats when hungry", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (player().getHungerManager().getFoodLevel() > minHunger.get()) {
            stopEating();
            return;
        }
        int foodSlot = findFood();
        if (foodSlot == -1) {
            stopEating();
            return;
        }
        if (!eating) {
            previousSlot = player().getInventory().getSelectedSlot();
            player().getInventory().setSelectedSlot(foodSlot);
            eating = true;
        }
        mc.interactionManager.interactItem(player(), Hand.MAIN_HAND);
    }

    private int findFood() {
        for (int i = 0; i < 9; i++) {
            if (player().getInventory().getStack(i).contains(DataComponentTypes.FOOD)) {
                return i;
            }
        }
        return -1;
    }

    private void stopEating() {
        if (eating) {
            if (previousSlot >= 0) {
                player().getInventory().setSelectedSlot(previousSlot);
            }
            eating = false;
        }
    }

    @Override
    protected void onDisable() {
        stopEating();
    }
}
