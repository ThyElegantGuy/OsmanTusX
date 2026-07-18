package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;

/** Renders WASD, jump and click keys, highlighting pressed inputs. */
public final class KeystrokesHud extends HudModule {

    private static final int SIZE = 16;
    private static final int GAP = 2;

    public KeystrokesHud() {
        super("Keystrokes", "Shows pressed movement keys", 0.02, 0.70);
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        // Row 1: W
        key(context, x + SIZE + GAP, y, "W", mc.options.forwardKey);
        // Row 2: A S D
        key(context, x, y + SIZE + GAP, "A", mc.options.leftKey);
        key(context, x + SIZE + GAP, y + SIZE + GAP, "S", mc.options.backKey);
        key(context, x + (SIZE + GAP) * 2, y + SIZE + GAP, "D", mc.options.rightKey);
        // Row 3: wide space bar
        double spaceY = y + (SIZE + GAP) * 2;
        boolean space = mc.options.jumpKey.isPressed();
        Render2DUtil.roundedRect(context, x, spaceY, getWidth(), SIZE - 4, 2,
                space ? OsmanTusX.THEMES.accent() : new Color(18, 20, 28, 200));
    }

    private void key(DrawContext context, double x, double y, String label, KeyBinding binding) {
        boolean pressed = binding.isPressed();
        Render2DUtil.roundedRect(context, x, y, SIZE, SIZE, 2,
                pressed ? OsmanTusX.THEMES.accent() : new Color(18, 20, 28, 200));
        Color textColor = pressed ? Color.BLACK : OsmanTusX.THEMES.getActive().text();
        Render2DUtil.centeredText(context, label, x + SIZE / 2.0, y + SIZE / 2.0 - 4, textColor);
    }

    @Override
    public double getWidth() {
        return SIZE * 3 + GAP * 2;
    }

    @Override
    public double getHeight() {
        return (SIZE + GAP) * 2 + (SIZE - 4);
    }
}
