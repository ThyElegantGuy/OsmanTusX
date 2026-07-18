package com.osmantusx.module.player;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/** Rapidly shift-clicks every item out of an open chest into the inventory. */
public final class ChestStealer extends Module {

    private long lastMove;

    public ChestStealer() {
        super("Chest Stealer", "Empties open containers", Category.PLAYER);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.interactionManager == null) {
            return;
        }
        if (!(mc.currentScreen instanceof GenericContainerScreen screen)) {
            return;
        }
        if (System.currentTimeMillis() - lastMove < 50) {
            return;
        }
        GenericContainerScreenHandler handler = screen.getScreenHandler();
        int containerSlots = handler.getRows() * 9;
        for (Slot slot : handler.slots) {
            if (slot.id < containerSlots && slot.hasStack()) {
                mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, player());
                lastMove = System.currentTimeMillis();
                return;
            }
        }
    }
}
