package com.osmantusx.module.hud;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.client.gui.DrawContext;

/**
 * Base class for draggable HUD elements.
 *
 * <p>Position is stored as a screen fraction (0..1) via hidden settings so it
 * persists across resolutions and configs. Each element renders itself every
 * HUD frame while enabled; the HUD editor reuses {@link #getPixelX()} /
 * {@link #getPixelY()} and {@link #getWidth()} / {@link #getHeight()} for
 * hit-testing and dragging.</p>
 */
public abstract class HudModule extends Module {

    private final DoubleSetting posX;
    private final DoubleSetting posY;

    protected HudModule(String name, String description, double defaultFractionX, double defaultFractionY) {
        super(name, description, Category.HUD);
        this.posX = add(new DoubleSetting("X", "Horizontal position", defaultFractionX, 0, 1, 0.0001));
        this.posY = add(new DoubleSetting("Y", "Vertical position", defaultFractionY, 0, 1, 0.0001));
        this.posX.visibleWhen(() -> false);
        this.posY.visibleWhen(() -> false);
    }

    /** Draws the element with its top-left at the given screen pixel position. */
    public abstract void render(DrawContext context, double x, double y);

    /** Current rendered width in pixels (used by the editor for hit-testing). */
    public abstract double getWidth();

    /** Current rendered height in pixels (used by the editor for hit-testing). */
    public abstract double getHeight();

    public double getPixelX() {
        return posX.get() * scaledWidth();
    }

    public double getPixelY() {
        return posY.get() * scaledHeight();
    }

    /** Moves the element, clamping so it stays on screen. */
    public void setPixelPosition(double pixelX, double pixelY) {
        double maxX = Math.max(1, scaledWidth() - getWidth());
        double maxY = Math.max(1, scaledHeight() - getHeight());
        posX.set(Math.max(0, Math.min(pixelX, maxX)) / scaledWidth());
        posY.set(Math.max(0, Math.min(pixelY, maxY)) / scaledHeight());
    }

    protected int scaledWidth() {
        return mc.getWindow().getScaledWidth();
    }

    protected int scaledHeight() {
        return mc.getWindow().getScaledHeight();
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.player == null) {
            return;
        }
        render(event.context(), getPixelX(), getPixelY());
    }
}
