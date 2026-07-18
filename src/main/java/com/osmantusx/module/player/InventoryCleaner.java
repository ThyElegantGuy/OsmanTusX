package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

import java.util.Set;

/** Drops configured junk items from the inventory over time. */
public final class InventoryCleaner extends Module {

    private static final Set<Item> JUNK = Set.of(
            Items.ROTTEN_FLESH, Items.POISONOUS_POTATO, Items.SPIDER_EYE,
            Items.COBBLESTONE, Items.DIRT, Items.GRAVEL, Items.FLINT);

    private long lastDrop;

    public InventoryCleaner() {
        super("Inventory Cleaner", "Drops junk items", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (System.currentTimeMillis() - lastDrop < 150) {
            return;
        }
        for (Slot slot : player().playerScreenHandler.slots) {
            if (slot.id >= 9 && JUNK.contains(slot.getStack().getItem())) {
                mc.interactionManager.clickSlot(player().playerScreenHandler.syncId,
                        slot.id, 1, SlotActionType.THROW, player());
                lastDrop = System.currentTimeMillis();
                break;
            }
        }
    }
}
