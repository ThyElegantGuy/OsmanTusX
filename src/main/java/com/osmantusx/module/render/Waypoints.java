package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.util.Waypoint;
import com.osmantusx.util.WaypointStore;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.gui.DrawContext;

/** Renders saved waypoints in the world with name and distance. */
public final class Waypoints extends Module {

    public Waypoints() {
        super("Waypoints", "Shows saved waypoints", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        for (Waypoint waypoint : WaypointStore.all()) {
            double[] screen = Render3DUtil.worldToScreen(waypoint.toVec());
            if (screen == null) {
                continue;
            }
            double distance = player().getEntityPos().distanceTo(waypoint.toVec());
            String label = waypoint.name() + " (" + (int) distance + "m)";
            Render2DUtil.rect(context, screen[0] - 2, screen[1] - 2, 4, 4, OsmanTusX.THEMES.accent());
            Render2DUtil.centeredText(context, label, screen[0], screen[1] - 14, OsmanTusX.THEMES.getActive().text());
        }
    }
}
