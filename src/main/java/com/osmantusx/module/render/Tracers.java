package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.ColorSetting;
import com.osmantusx.setting.DoubleSetting;
import com.osmantusx.setting.EnumSetting;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/** Draws lines from a screen anchor to nearby entities. */
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
    private final DoubleSetting width = add(new DoubleSetting("Width", "Line thickness", 1.6, 1, 5, 0.1));
    private final IntSetting range = add(new IntSetting("Range", "Max distance in blocks", 128, 4, 256));
    private final ColorSetting color = add(new ColorSetting("Color", "Line color", new Color(120, 90, 255)));

    public Tracers() {
        super("Tracers", "Lines pointing to entities", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        double screenW = mc.getWindow().getScaledWidth();
        double screenH = mc.getWindow().getScaledHeight();
        double originX = screenW / 2.0;
        double originY = location.get() == Location.MOUSE ? screenH / 2.0 : screenH;
        int maxSq = range.get() * range.get();
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
            double[] screen = Render3DUtil.worldToScreen(living.getBoundingBox().getCenter());
            if (screen == null) {
                continue;
            }
            Color c = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            Render2DUtil.line(context, originX, originY, screen[0], screen[1], c, width.get());
        }
    }
}
