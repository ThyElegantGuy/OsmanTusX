package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
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
            Color color = isPlayer ? OsmanTusX.THEMES.accent() : Color.RED;
            Render2DUtil.outline(context, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1], color);
        }
    }

    /** @return {@code [minX, minY, maxX, maxY]} screen rectangle, or null if off-screen. */
    public static double[] projectBox(Box box) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        boolean any = false;
        for (int i = 0; i < 8; i++) {
            double x = (i & 1) == 0 ? box.minX : box.maxX;
            double y = (i & 2) == 0 ? box.minY : box.maxY;
            double z = (i & 4) == 0 ? box.minZ : box.maxZ;
            double[] screen = Render3DUtil.worldToScreen(new Vec3d(x, y, z));
            if (screen == null) {
                continue;
            }
            any = true;
            minX = Math.min(minX, screen[0]);
            minY = Math.min(minY, screen[1]);
            maxX = Math.max(maxX, screen[0]);
            maxY = Math.max(maxY, screen[1]);
        }
        return any ? new double[]{minX, minY, maxX, maxY} : null;
    }
}
