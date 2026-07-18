package com.osmantusx.module.world;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.util.Waypoint;
import com.osmantusx.util.WaypointStore;

/**
 * Captures the player's current position as a named waypoint each time it is
 * enabled. Waypoints are drawn by the {@code Waypoints} render module and can
 * also be managed with the {@code .waypoint} command.
 */
public final class WaypointManager extends Module {

    private int counter = 1;

    public WaypointManager() {
        super("Waypoint Manager", "Saves your location as a waypoint", Category.WORLD);
    }

    @Override
    protected void onEnable() {
        if (inGame()) {
            String name = "WP-" + counter++;
            WaypointStore.add(new Waypoint(name, player().getX(), player().getY(), player().getZ()));
            OsmanTusX.NOTIFICATIONS.info("Saved waypoint " + name);
        }
    }
}
