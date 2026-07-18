package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/** Keeps a Totem of Undying in the off-hand slot whenever one is available. */
public final class AutoTotem extends Module {

    /** Hotbar button 40 in {@code SWAP} maps to the off-hand slot. */
    private static final int OFFHAND_SWAP_BUTTON = 40;

    public AutoTotem() {
        super("Auto Totem", "Auto-equips a totem to the off-hand", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (player().getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }
        for (Slot slot : player().playerScreenHandler.slots) {
            if (slot.getStack().getItem() == Items.TOTEM_OF_UNDYING) {
                mc.interactionManager.clickSlot(player().playerScreenHandler.syncId,
                        slot.id, OFFHAND_SWAP_BUTTON, SlotActionType.SWAP, player());
                break;
            }
        }
    }
}
