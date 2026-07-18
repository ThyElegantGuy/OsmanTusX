package com.osmantusx.util.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Vector3f;

/**
 * Rendering helpers for the world (3D) pass and, for block/item finders, the
 * cheaper 2D screen projection.
 *
 * <p>Entity ESP/Chams/Tracers render in the world pass exactly the way Meteor
 * and Wurst do: geometry is buffered into a self-managed immediate buffer using
 * the vanilla {@code lines} / {@code debugFilledBox} render layers and flushed
 * on the spot with {@link #flush()}. This is crisp at any distance and has no
 * per-tick screen-projection delay.</p>
 *
 * <p>The 2D projection maths implement a standard pinhole camera model using
 * the game's vertical FOV and the current camera basis vectors.</p>
 */
public final class Render3DUtil {

    private Render3DUtil() {
    }

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final Vec3d WORLD_UP = new Vec3d(0, 1, 0);

    private static final BufferAllocator ALLOCATOR = new BufferAllocator(0x200000);
    private static final VertexConsumerProvider.Immediate IMMEDIATE =
            VertexConsumerProvider.immediate(ALLOCATOR);

    private static Vec3d cameraPos() {
        return MC.gameRenderer.getCamera().getCameraPos();
    }

    /**
     * Draws a crisp world-space outline box around {@code box}. Buffered into
     * the shared immediate buffer; call {@link #flush()} once all boxes for the
     * frame are queued.
     */
    public static void drawBoxOutline(MatrixStack matrices, Box box, int argb, float lineWidth) {
        Vec3d cam = cameraPos();
        VertexConsumer buffer = IMMEDIATE.getBuffer(RenderLayers.lines());
        VertexRendering.drawOutline(matrices, buffer, VoxelShapes.cuboid(box),
                -cam.x, -cam.y, -cam.z, argb, Math.max(1.0f, lineWidth));
    }

    /** Draws a translucent world-space filled box over {@code box}. */
    public static void drawFilledBox(MatrixStack matrices, Box box, int argb) {
        Vec3d cam = cameraPos();
        VertexConsumer vc = IMMEDIATE.getBuffer(RenderLayers.debugFilledBox());
        MatrixStack.Entry entry = matrices.peek();

        float x1 = (float) (box.minX - cam.x);
        float y1 = (float) (box.minY - cam.y);
        float z1 = (float) (box.minZ - cam.z);
        float x2 = (float) (box.maxX - cam.x);
        float y2 = (float) (box.maxY - cam.y);
        float z2 = (float) (box.maxZ - cam.z);

        quad(vc, entry, argb, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        quad(vc, entry, argb, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
        quad(vc, entry, argb, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
        quad(vc, entry, argb, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        quad(vc, entry, argb, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
        quad(vc, entry, argb, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
    }

    /** Draws a world-space line from {@code start} to {@code end}. */
    public static void drawLine(MatrixStack matrices, Vec3d start, Vec3d end, int argb, float lineWidth) {
        Vec3d cam = cameraPos();
        VertexConsumer vc = IMMEDIATE.getBuffer(RenderLayers.lines());
        MatrixStack.Entry entry = matrices.peek();

        float x1 = (float) (start.x - cam.x);
        float y1 = (float) (start.y - cam.y);
        float z1 = (float) (start.z - cam.z);
        float x2 = (float) (end.x - cam.x);
        float y2 = (float) (end.y - cam.y);
        float z2 = (float) (end.z - cam.z);

        Vector3f normal = new Vector3f(x2 - x1, y2 - y1, z2 - z1);
        if (normal.lengthSquared() < 1.0e-6f) {
            return;
        }
        normal.normalize();
        float w = Math.max(1.0f, lineWidth);
        vc.vertex(entry, x1, y1, z1).color(argb).normal(entry, normal).lineWidth(w);
        vc.vertex(entry, x2, y2, z2).color(argb).normal(entry, normal).lineWidth(w);
    }

    /** Flushes all queued world-space geometry to the screen. */
    public static void flush() {
        IMMEDIATE.draw();
    }

    private static void quad(VertexConsumer vc, MatrixStack.Entry e, int argb,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz) {
        vc.vertex(e, ax, ay, az).color(argb);
        vc.vertex(e, bx, by, bz).color(argb);
        vc.vertex(e, cx, cy, cz).color(argb);
        vc.vertex(e, dx, dy, dz).color(argb);
    }

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
