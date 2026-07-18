package com.osmantusx.util;

import net.minecraft.util.math.Vec3d;

/** Immutable named world location used by the waypoint modules. */
public record Waypoint(String name, double x, double y, double z) {

    public Vec3d toVec() {
        return new Vec3d(x, y, z);
    }
}
