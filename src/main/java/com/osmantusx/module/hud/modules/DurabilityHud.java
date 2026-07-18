package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import net.minecraft.item.ItemStack;

/** Displays the durability of the held item. */
public final class DurabilityHud extends TextHudModule {

    public DurabilityHud() {
        super("Durability Display", "Shows held item durability", 0.01, 0.44);
    }

    @Override
    protected String getText() {
        if (mc.player == null) {
            return "Durability: -";
        }
        ItemStack stack = mc.player.getMainHandStack();
        if (stack.isEmpty() || !stack.isDamageable()) {
            return "Durability: -";
        }
        int remaining = stack.getMaxDamage() - stack.getDamage();
        return "Durability: " + remaining + "/" + stack.getMaxDamage();
    }
}
