package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Displays how many Totems of Undying are in the inventory. */
public final class TotemCounterHud extends TextHudModule {

    public TotemCounterHud() {
        super("Totem Counter", "Shows totem count", 0.01, 0.41);
    }

    @Override
    protected String getText() {
        int totems = 0;
        if (mc.player != null) {
            for (ItemStack stack : mc.player.getInventory().getMainStacks()) {
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    totems += stack.getCount();
                }
            }
            if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
                totems += mc.player.getOffHandStack().getCount();
            }
        }
        return "Totems: " + totems;
    }
}
