package com.osmantusx.event.events;

import com.osmantusx.event.Event;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;

/**
 * Fired during world rendering. Wraps Fabric's {@link WorldRenderContext} so
 * modules can access the matrix stack, camera and tick delta if they need true
 * 3D rendering (the built-in ESP uses cheaper 2D projection instead).
 */
public final class Render3DEvent extends Event {

    private final WorldRenderContext context;

    public Render3DEvent(WorldRenderContext context) {
        this.context = context;
    }

    public WorldRenderContext context() {
        return context;
    }
}
