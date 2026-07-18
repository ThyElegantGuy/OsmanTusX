package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.ColorSetting;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/** Draws 2D bounding boxes around living entities using screen projection. */
public final class Esp extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Highlight players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Highlight mobs", true));
    private final IntSetting width = add(new IntSetting("Width", "Box line width", 1, 1, 5));
    private final ColorSetting color = add(new ColorSetting("Color", "Box color", new Color(120, 90, 255)));

    public Esp() {
        super("ESP", "Shows boxes around entities", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof LivingEntity living) || living == player() || living.isDead()) {
                continue;
            }
            boolean isPlayer = living instanceof PlayerEntity;
            if (isPlayer && !players.get()) {
                continue;
            }
            if (!isPlayer && !mobs.get()) {
                continue;
            }
            double[] bounds = projectBox(living.getBoundingBox());
            if (bounds == null) {
                continue;
            }
            Color c = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            Render2DUtil.outline(context, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1],
                    c, width.get());
        }
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
            double[] screen = Render3DUtil.worldToScreen(new Vec3d(x, y, z));
            if (screen == null) {
                // A corner behind the camera makes the 2D hull meaningless.
                return null;
            }
            minX = Math.min(minX, screen[0]);
            minY = Math.min(minY, screen[1]);
            maxX = Math.max(maxX, screen[0]);
            maxY = Math.max(maxY, screen[1]);
        }
        int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
        minX = Math.max(0, Math.min(minX, sw));
        maxX = Math.max(0, Math.min(maxX, sw));
        minY = Math.max(0, Math.min(minY, sh));
        maxY = Math.max(0, Math.min(maxY, sh));
        if (maxX - minX < 1 || maxY - minY < 1) {
            return null;
        }
        return new double[]{minX, minY, maxX, maxY};
    }
}
