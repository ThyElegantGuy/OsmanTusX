package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Refills the selected hotbar slot from a matching stack elsewhere in the
 * inventory once it drops below the configured threshold.
 */
public final class Refill extends Module {

    private final IntSetting threshold = add(new IntSetting("Threshold", "Refill below", 8, 1, 32));

    public Refill() {
        super("Refill", "Tops up the held stack", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        int selected = player().getInventory().getSelectedSlot();
        ItemStack held = player().getInventory().getStack(selected);
        if (held.isEmpty() || held.getCount() > threshold.get()) {
            return;
        }
        int hotbarSlotId = 36 + selected;
        for (Slot slot : player().playerScreenHandler.slots) {
            if (slot.id >= 9 && slot.id != hotbarSlotId
                    && ItemStack.areItemsEqual(slot.getStack(), held)) {
                mc.interactionManager.clickSlot(player().playerScreenHandler.syncId,
                        slot.id, selected, SlotActionType.SWAP, player());
                break;
            }
        }
    }
}
