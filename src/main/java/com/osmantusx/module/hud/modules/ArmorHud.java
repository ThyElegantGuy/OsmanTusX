package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Renders the player's equipped armour with durability overlays. */
public final class ArmorHud extends HudModule {

    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHud() {
        super("Armor HUD", "Shows equipped armour", 0.45, 0.80);
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        if (mc.player == null) {
            return;
        }
        int slotX = (int) x;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = mc.player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX, (int) y);
                context.drawStackOverlay(mc.textRenderer, stack, slotX, (int) y);
            }
            slotX += 18;
        }
    }

    @Override
    public double getWidth() {
        return 18 * SLOTS.length;
    }

    @Override
    public double getHeight() {
        return 18;
    }
}
