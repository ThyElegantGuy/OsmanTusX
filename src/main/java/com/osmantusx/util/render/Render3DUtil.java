package com.osmantusx.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
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

    /**
     * Projects a world-space box to a screen rectangle.
     *
     * @return {@code [minX, minY, maxX, maxY]} clamped to the screen, or
     *         {@code null} when any corner is behind the camera (which would
     *         otherwise produce a wildly wrong, screen-spanning rectangle).
     */
    public static double[] projectBox(Box box) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            double x = (i & 1) == 0 ? box.minX : box.maxX;
            double y = (i & 2) == 0 ? box.minY : box.maxY;
            double z = (i & 4) == 0 ? box.minZ : box.maxZ;
            double[] screen = worldToScreen(new Vec3d(x, y, z));
            if (screen == null) {
                return null;
            }
            minX = Math.min(minX, screen[0]);
            minY = Math.min(minY, screen[1]);
            maxX = Math.max(maxX, screen[0]);
            maxY = Math.max(maxY, screen[1]);
        }
        int sw = MC.getWindow().getScaledWidth();
        int sh = MC.getWindow().getScaledHeight();
        minX = Math.max(0, Math.min(minX, sw));
        maxX = Math.max(0, Math.min(maxX, sw));
        minY = Math.max(0, Math.min(minY, sh));
        maxY = Math.max(0, Math.min(maxY, sh));
        if (maxX - minX < 1 || maxY - minY < 1) {
            return null;
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    /**
     * Projects a box to a screen rectangle, tolerating corners behind the
     * camera. The box is shown whenever its center is in front of the camera
     * (the same visibility rule tracers use), so ESP no longer drops mobs that
     * tracers still point at. Corners behind the camera are ignored and the
     * hull is clamped to the screen.
     *
     * @return {@code [minX, minY, maxX, maxY]} or {@code null} when the center
     *         is behind the camera or the rectangle is degenerate.
     */
    public static double[] projectBoxLoose(Box box) {
        double[] center = worldToScreen(box.getCenter());
        if (center == null) {
            return null;
        }
        double minX = center[0];
        double minY = center[1];
        double maxX = center[0];
        double maxY = center[1];
        for (int i = 0; i < 8; i++) {
            double x = (i & 1) == 0 ? box.minX : box.maxX;
            double y = (i & 2) == 0 ? box.minY : box.maxY;
            double z = (i & 4) == 0 ? box.minZ : box.maxZ;
            double[] screen = worldToScreen(new Vec3d(x, y, z));
            if (screen == null) {
                continue;
            }
            minX = Math.min(minX, screen[0]);
            minY = Math.min(minY, screen[1]);
            maxX = Math.max(maxX, screen[0]);
            maxY = Math.max(maxY, screen[1]);
        }
        int sw = MC.getWindow().getScaledWidth();
        int sh = MC.getWindow().getScaledHeight();
        minX = Math.max(0, Math.min(minX, sw));
        maxX = Math.max(0, Math.min(maxX, sw));
        minY = Math.max(0, Math.min(minY, sh));
        maxY = Math.max(0, Math.min(maxY, sh));
        if (maxX - minX < 1 || maxY - minY < 1) {
            return null;
        }
        return new double[]{minX, minY, maxX, maxY};
    }

    /**
     * Bounding box built from the entity's render-interpolated position so the
     * box tracks the model smoothly (no per-tick delay) instead of snapping
     * once per tick.
     */
    public static Box interpolatedBox(Entity entity) {
        float td = MC.getRenderTickCounter().getTickProgress(true);
        Vec3d pos = entity.getLerpedPos(td);
        double halfW = entity.getWidth() / 2.0;
        double height = entity.getHeight();
        return new Box(pos.x - halfW, pos.y, pos.z - halfW,
                pos.x + halfW, pos.y + height, pos.z + halfW);
    }
}
