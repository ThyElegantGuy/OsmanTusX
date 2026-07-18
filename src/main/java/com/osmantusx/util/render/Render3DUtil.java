package com.osmantusx.util.render;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

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
     * Draws a crisp 3D outline box around {@code box} in world space during the
     * world render pass. Unlike screen projection this has no per-frame delay
     * and stays sharp at any distance.
     */
    public static void drawBoxOutline(WorldRenderContext ctx, Box box, Color color, float lineWidth) {
        MatrixStack matrices = ctx.matrices();
        if (matrices == null) {
            return;
        }
        Vec3d cam = MC.gameRenderer.getCamera().getCameraPos();
        VertexConsumer buffer = ctx.consumers().getBuffer(RenderLayers.lines());
        VertexRendering.drawOutline(matrices, buffer, VoxelShapes.cuboid(box),
                -cam.x, -cam.y, -cam.z, color.argb(), Math.max(1.0f, lineWidth));
    }

    /**
     * Fills a translucent 3D box over {@code box} that shows through walls, used
     * for chams so it reads clearly differently from the ESP outline.
     */
    public static void drawFilledBox(WorldRenderContext ctx, Box box, Color color) {
        MatrixStack matrices = ctx.matrices();
        if (matrices == null) {
            return;
        }
        Vec3d cam = MC.gameRenderer.getCamera().getCameraPos();
        VertexConsumer vc = ctx.consumers().getBuffer(RenderLayers.debugFilledBox());
        MatrixStack.Entry entry = matrices.peek();
        int argb = color.argb();
        float x1 = (float) (box.minX - cam.x);
        float y1 = (float) (box.minY - cam.y);
        float z1 = (float) (box.minZ - cam.z);
        float x2 = (float) (box.maxX - cam.x);
        float y2 = (float) (box.maxY - cam.y);
        float z2 = (float) (box.maxZ - cam.z);
        // Bottom and top.
        quad(vc, entry, argb, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        quad(vc, entry, argb, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
        // North and south.
        quad(vc, entry, argb, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
        quad(vc, entry, argb, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        // West and east.
        quad(vc, entry, argb, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
        quad(vc, entry, argb, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
    }

    /**
     * Bounding box built from the entity's render-interpolated position so the
     * box tracks the model smoothly instead of snapping once per tick.
     */
    public static Box interpolatedBox(Entity entity) {
        float td = MC.getRenderTickCounter().getTickProgress(true);
        Vec3d pos = entity.getLerpedPos(td);
        double halfW = entity.getWidth() / 2.0;
        double height = entity.getHeight();
        return new Box(pos.x - halfW, pos.y, pos.z - halfW,
                pos.x + halfW, pos.y + height, pos.z + halfW);
    }

    private static void quad(VertexConsumer vc, MatrixStack.Entry e, int argb,
                             float ax, float ay, float az, float bx, float by, float bz,
                             float cx, float cy, float cz, float dx, float dy, float dz) {
        vc.vertex(e, ax, ay, az).color(argb);
        vc.vertex(e, bx, by, bz).color(argb);
        vc.vertex(e, cx, cy, cz).color(argb);
        vc.vertex(e, dx, dy, dz).color(argb);
    }
}
