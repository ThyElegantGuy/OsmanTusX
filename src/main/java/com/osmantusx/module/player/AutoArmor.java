package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Automatically equips armour pieces found in the inventory into empty armour
 * slots by shift-clicking them (which vanilla routes to the correct slot).
 */
public final class AutoArmor extends Module {

    private long lastAction;

    public AutoArmor() {
        super("Auto Armor", "Equips armour from your inventory", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        // Throttle so we equip one piece at a time as slots update.
        if (System.currentTimeMillis() - lastAction < 200) {
            return;
        }
        for (Slot slot : player().playerScreenHandler.slots) {
            ItemStack stack = slot.getStack();
            EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
            if (slot.id >= 9 && equippable != null && equippable.slot().isArmorSlot()) {
                mc.interactionManager.clickSlot(player().playerScreenHandler.syncId,
                        slot.id, 0, SlotActionType.QUICK_MOVE, player());
                lastAction = System.currentTimeMillis();
                break;
            }
        }
    }
}
