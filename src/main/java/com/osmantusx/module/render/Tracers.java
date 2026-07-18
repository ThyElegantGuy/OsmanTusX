package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render3DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.ColorSetting;
import com.osmantusx.setting.DoubleSetting;
import com.osmantusx.setting.EnumSetting;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Draws world-space lines from a screen anchor to nearby entities. */
public final class Tracers extends Module {

    /** Where the tracer lines originate on the screen. */
    public enum Location {
        MOUSE,
        BOTTOM
    }

    private final BooleanSetting players = add(new BooleanSetting("Players", "Trace players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Trace mobs", true));
    private final EnumSetting<Location> location =
            add(new EnumSetting<>("Location", "Where lines start from", Location.BOTTOM));
    private final DoubleSetting width = add(new DoubleSetting("Width", "Line thickness", 1.5, 1, 5, 0.1));
    private final IntSetting range = add(new IntSetting("Range", "Max distance in blocks", 128, 4, 256));
    private final ColorSetting color = add(new ColorSetting("Color", "Line color", new Color(120, 90, 255)));

    public Tracers() {
        super("Tracers", "Lines pointing to entities", Category.RENDER);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (!inGame()) {
            return;
        }
        MatrixStack matrices = event.context().matrices();
        if (matrices == null) {
            return;
        }
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getCameraPos();
        Vec3d forward = Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).normalize();
        Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d up = right.crossProduct(forward).normalize();

        // Anchor the lines just in front of the camera so they fan out from the
        // crosshair (MOUSE) or from the bottom of the screen (BOTTOM).
        Vec3d origin = camPos.add(forward.multiply(1.0));
        if (location.get() == Location.BOTTOM) {
            origin = origin.subtract(up.multiply(0.9));
        }

        int maxSq = range.get() * range.get();
        boolean drew = false;
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof LivingEntity living) || living == player() || living.isDead()) {
                continue;
            }
            boolean isPlayer = living instanceof PlayerEntity;
            if ((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
                continue;
            }
            if (player().squaredDistanceTo(living) > maxSq) {
                continue;
            }
            Vec3d target = Render3DUtil.interpolatedBox(living).getCenter();
            Color c = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            Render3DUtil.drawLine(matrices, origin, target, c.argb(), width.get().floatValue());
            drew = true;
        }
        if (drew) {
            Render3DUtil.flush();
        }
    }
}
