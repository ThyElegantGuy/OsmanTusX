package com.osmantusx.module.hud;

import com.osmantusx.OsmanTusX;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;

/**
 * Convenience base for single-line HUD elements. Subclasses only implement
 * {@link #getText()}; this class handles the rounded background, accent bar and
 * text layout so every text element looks consistent.
 */
public abstract class TextHudModule extends HudModule {

    private static final double PADDING = 4;
    private static final double HEIGHT = 13;

    private String cached = "";

    protected TextHudModule(String name, String description, double fractionX, double fractionY) {
        super(name, description, fractionX, fractionY);
    }

    /** @return the text to display; recomputed each frame. */
    protected abstract String getText();

    @Override
    public void render(DrawContext context, double x, double y) {
        cached = getText();
        double width = getWidth();
        Render2DUtil.roundedRect(context, x, y, width, HEIGHT, 2, new Color(18, 20, 28, 200));
        Render2DUtil.rect(context, x, y, 2, HEIGHT, OsmanTusX.THEMES.accent());
        Render2DUtil.text(context, cached, x + PADDING + 1, y + 3, OsmanTusX.THEMES.getActive().text());
    }

    @Override
    public double getWidth() {
        String text = cached.isEmpty() ? getName() : cached;
        return Render2DUtil.textWidth(text) + PADDING * 2 + 2;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }
}
