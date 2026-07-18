package com.osmantusx.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * World-to-screen projection for 2D ESP/Tracers, using the exact model-view and
 * projection matrices the game renders with (captured each frame by
 * {@code WorldRendererMixin}). This mirrors Meteor Client's {@code NametagUtils}
 * approach, so projected boxes line up perfectly with entities and have none of
 * the distortion of a hand-rolled pinhole model.
 */
public final class Render3DUtil {

    private Render3DUtil() {
    }

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    private static final Matrix4f MODEL_VIEW = new Matrix4f();
    private static final Matrix4f PROJECTION = new Matrix4f();
    private static double camX;
    private static double camY;
    private static double camZ;
    private static boolean ready;

    /** Captures the frame's matrices/camera; called from the world render mixin. */
    public static void updateMatrices(Matrix4f modelView, Matrix4f projection,
                                      double cameraX, double cameraY, double cameraZ) {
        MODEL_VIEW.set(modelView);
        PROJECTION.set(projection);
        camX = cameraX;
        camY = cameraY;
        camZ = cameraZ;
        ready = true;
    }

    /**
     * Converts a world position to GUI-space screen coordinates.
     *
     * @return an {@code [x, y]} pair, or {@code null} when the point is behind
     *         the camera.
     */
    public static double[] worldToScreen(Vec3d worldPos) {
        if (!ready) {
            return null;
        }
        Vector4f pos = new Vector4f(
                (float) (worldPos.x - camX),
                (float) (worldPos.y - camY),
                (float) (worldPos.z - camZ), 1.0f);
        pos.mul(MODEL_VIEW);
        pos.mul(PROJECTION);
        if (pos.w <= 0.0f) {
            return null;
        }
        float inv = 1.0f / pos.w * 0.5f;
        double ndcX = pos.x * inv + 0.5f;
        double ndcY = pos.y * inv + 0.5f;

        double screenX = ndcX * MC.getWindow().getScaledWidth();
        double screenY = (1.0 - ndcY) * MC.getWindow().getScaledHeight();
        if (!Double.isFinite(screenX) || !Double.isFinite(screenY)) {
            return null;
        }
        return new double[]{screenX, screenY};
    }

    /**
     * Projects a world-space box to a screen rectangle. Seeded with the box
     * centre so an entity is still boxed when a corner slips behind the camera,
     * with corners behind the camera ignored rather than warping the hull.
     *
     * @return {@code [minX, minY, maxX, maxY]} clamped to the screen, or
     *         {@code null} when the centre is behind the camera or the
     *         rectangle is degenerate.
     */
    public static double[] projectBox(Box box) {
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
