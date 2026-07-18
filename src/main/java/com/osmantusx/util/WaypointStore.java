package com.osmantusx.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Simple in-memory registry of waypoints shared by the render and world modules. */
public final class WaypointStore {

    private WaypointStore() {
    }

    private static final List<Waypoint> WAYPOINTS = new ArrayList<>();

    public static void add(Waypoint waypoint) {
        WAYPOINTS.add(waypoint);
    }

    public static boolean remove(String name) {
        return WAYPOINTS.removeIf(w -> w.name().equalsIgnoreCase(name));
    }

    public static List<Waypoint> all() {
        return Collections.unmodifiableList(WAYPOINTS);
    }
}
