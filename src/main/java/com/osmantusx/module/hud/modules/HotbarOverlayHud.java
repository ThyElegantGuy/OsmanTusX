package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Renders a compact copy of the hotbar with the selected slot highlighted. */
public final class HotbarOverlayHud extends HudModule {

    private static final int CELL = 18;

    public HotbarOverlayHud() {
        super("Hotbar Overlay", "Shows a compact hotbar", 0.35, 0.90);
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        if (mc.player == null) {
            return;
        }
        Render2DUtil.roundedRect(context, x, y, getWidth(), getHeight(), 3, new Color(18, 20, 28, 200));
        int selected = mc.player.getInventory().getSelectedSlot();
        for (int i = 0; i < 9; i++) {
            int px = (int) x + 2 + i * CELL;
            int py = (int) y + 2;
            if (i == selected) {
                Render2DUtil.outline(context, px - 1, py - 1, CELL, CELL, OsmanTusX.THEMES.accent());
            }
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                context.drawItem(stack, px, py);
                context.drawStackOverlay(mc.textRenderer, stack, px, py);
            }
        }
    }

    @Override
    public double getWidth() {
        return 9 * CELL + 4;
    }

    @Override
    public double getHeight() {
        return CELL + 4;
    }
}
