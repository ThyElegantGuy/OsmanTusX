package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Demonstrates automated crafting by turning planks into sticks using the 2x2
 * survival inventory grid. It places planks into the vertical grid pair and
 * shift-collects the resulting sticks, one step per throttle window.
 */
public final class AutoCraft extends Module {

    private static final int RESULT_SLOT = 0;
    private static final int GRID_TOP = 1;
    private static final int GRID_BOTTOM = 3;

    private long lastStep;

    public AutoCraft() {
        super("Auto Craft", "Auto-crafts sticks from planks", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (System.currentTimeMillis() - lastStep < 250) {
            return;
        }
        int syncId = player().playerScreenHandler.syncId;

        // Phase 2: collect crafted sticks if the recipe has produced any.
        ItemStack result = player().playerScreenHandler.getSlot(RESULT_SLOT).getStack();
        if (result.getItem() == Items.STICK) {
            mc.interactionManager.clickSlot(syncId, RESULT_SLOT, 0, SlotActionType.QUICK_MOVE, player());
            lastStep = System.currentTimeMillis();
            return;
        }

        // Phase 1: seed the grid with one plank in each vertical slot.
        if (isEmpty(GRID_TOP) && isEmpty(GRID_BOTTOM)) {
            Slot plankSlot = findPlanks();
            if (plankSlot == null) {
                return;
            }
            mc.interactionManager.clickSlot(syncId, plankSlot.id, 0, SlotActionType.PICKUP, player());
            mc.interactionManager.clickSlot(syncId, GRID_TOP, 1, SlotActionType.PICKUP, player());
            mc.interactionManager.clickSlot(syncId, GRID_BOTTOM, 1, SlotActionType.PICKUP, player());
            mc.interactionManager.clickSlot(syncId, plankSlot.id, 0, SlotActionType.PICKUP, player());
            lastStep = System.currentTimeMillis();
        }
    }

    private boolean isEmpty(int slotId) {
        return player().playerScreenHandler.getSlot(slotId).getStack().isEmpty();
    }

    private Slot findPlanks() {
        for (Slot slot : player().playerScreenHandler.slots) {
            if (slot.id < 9) {
                continue;
            }
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.getCount() >= 2
                    && Registries.ITEM.getId(stack.getItem()).getPath().endsWith("_planks")) {
                return slot;
            }
        }
        return null;
    }
}
