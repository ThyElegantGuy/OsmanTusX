package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Renders the 27 main inventory slots as a 9x3 grid. */
public final class InventoryHud extends HudModule {

    private static final int COLS = 9;
    private static final int ROWS = 3;
    private static final int CELL = 18;

    public InventoryHud() {
        super("Inventory HUD", "Shows your main inventory", 0.02, 0.55);
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        if (mc.player == null) {
            return;
        }
        Render2DUtil.roundedRect(context, x, y, getWidth(), getHeight(), 3, new Color(18, 20, 28, 200));
        for (int i = 0; i < COLS * ROWS; i++) {
            int col = i % COLS;
            int row = i / COLS;
            int px = (int) x + 2 + col * CELL;
            int py = (int) y + 2 + row * CELL;
            ItemStack stack = mc.player.getInventory().getStack(9 + i);
            if (!stack.isEmpty()) {
                context.drawItem(stack, px, py);
                context.drawStackOverlay(mc.textRenderer, stack, px, py);
            }
        }
    }

    @Override
    public double getWidth() {
        return COLS * CELL + 4;
    }

    @Override
    public double getHeight() {
        return ROWS * CELL + 4;
    }
}
