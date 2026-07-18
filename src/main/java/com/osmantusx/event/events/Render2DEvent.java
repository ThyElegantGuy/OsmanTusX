package com.osmantusx.event.events;

import com.osmantusx.event.Event;
import net.minecraft.client.gui.DrawContext;

/**
 * Fired while the in-game HUD is being drawn. Modules use this to render 2D
 * overlays (HUD elements, projected ESP, notifications, etc.).
 */
public final class Render2DEvent extends Event {

    private final DrawContext context;
    private final float tickDelta;

    public Render2DEvent(DrawContext context, float tickDelta) {
        this.context = context;
        this.tickDelta = tickDelta;
    }

    public DrawContext context() {
        return context;
    }

    public float tickDelta() {
        return tickDelta;
    }
}
