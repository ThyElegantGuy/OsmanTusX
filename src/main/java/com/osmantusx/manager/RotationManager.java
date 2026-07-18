package com.osmantusx.manager;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.util.Wrapper;
import net.minecraft.util.math.Vec3d;

/**
 * Coordinates rotation requests from combat/world modules.
 *
 * <p>Modules request a target yaw/pitch each tick; the manager applies the most
 * recent request to the player. This is a straightforward client-side rotation
 * (the camera follows), which keeps the implementation portable. A silent /
 * body-only variant would require a packet mixin and can be layered on later.</p>
 */
public final class RotationManager implements Wrapper {

    private float targetYaw;
    private float targetPitch;
    private boolean rotationRequested;

    /** Requests the player look towards a specific point this tick. */
    public void lookAt(Vec3d point) {
        if (player() == null) {
            return;
        }
        Vec3d eyes = player().getEyePos();
        double dx = point.x - eyes.x;
        double dy = point.y - eyes.y;
        double dz = point.z - eyes.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);

        this.targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0f;
        this.targetPitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        this.rotationRequested = true;
    }

    public void setRotation(float yaw, float pitch) {
        this.targetYaw = yaw;
        this.targetPitch = pitch;
        this.rotationRequested = true;
    }

    public float getTargetYaw() {
        return targetYaw;
    }

    public float getTargetPitch() {
        return targetPitch;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || player() == null) {
            return;
        }
        if (rotationRequested) {
            player().setYaw(targetYaw);
            player().setPitch(Math.max(-90, Math.min(90, targetPitch)));
            rotationRequested = false;
        }
    }
}
