package com.osmantusx.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

/**
 * Projects world coordinates onto the 2D screen so ESP/Tracers can be drawn
 * cheaply through {@link net.minecraft.client.gui.DrawContext} during the HUD
 * pass, avoiding the fragile low-level 3D vertex APIs.
 *
 * <p>The maths implement a standard pinhole camera model using the game's
 * vertical FOV and the current camera basis vectors.</p>
 */
public final class Render3DUtil {

    private Render3DUtil() {
    }

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final Vec3d WORLD_UP = new Vec3d(0, 1, 0);

    /**
     * Converts a world position to GUI-space screen coordinates.
     *
     * @return an {@code [x, y]} pair, or {@code null} when the point is behind
     *         the camera or outside the projectable range.
     */
    public static double[] worldToScreen(Vec3d worldPos) {
        Camera camera = MC.gameRenderer.getCamera();
        if (camera == null) {
            return null;
        }

        Vec3d camPos = camera.getCameraPos();
        double yaw = Math.toRadians(camera.getYaw());
        double pitch = Math.toRadians(camera.getPitch());

        // Camera basis: forward from yaw/pitch, then right/up via cross products.
        Vec3d forward = Vec3d.fromPolar((float) Math.toDegrees(pitch), (float) Math.toDegrees(yaw)).normalize();
        Vec3d right = forward.crossProduct(WORLD_UP).normalize();
        Vec3d up = right.crossProduct(forward).normalize();

        Vec3d delta = worldPos.subtract(camPos);
        double camRight = delta.dotProduct(right);
        double camUp = delta.dotProduct(up);
        double camForward = delta.dotProduct(forward);
        if (camForward < 0.05) {
            return null; // Behind or on the camera plane.
        }

        int width = MC.getWindow().getScaledWidth();
        int height = MC.getWindow().getScaledHeight();
        double aspect = (double) width / height;

        double fov = MC.options.getFov().getValue();
        double tanHalf = Math.tan(Math.toRadians(fov) / 2.0);

        double ndcX = (camRight / camForward) / (tanHalf * aspect);
        double ndcY = (camUp / camForward) / tanHalf;

        double screenX = (ndcX + 1.0) / 2.0 * width;
        double screenY = (1.0 - ndcY) / 2.0 * height;
        return new double[]{screenX, screenY};
    }
}
