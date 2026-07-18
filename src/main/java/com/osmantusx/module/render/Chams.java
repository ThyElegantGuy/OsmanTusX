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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * 2D Chams: a translucent filled rectangle over each entity (plus optional
 * border), clearly distinct from ESP's plain outline.
 */
public final class Chams extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Overlay players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Overlay mobs", true));
    private final BooleanSetting outline = add(new BooleanSetting("Outline", "Draw a border", true));
    private final IntSetting width = add(new IntSetting("Width", "Outline width", 1, 1, 5));
    private final IntSetting opacity = add(new IntSetting("Opacity", "Fill opacity", 90, 20, 220));
    private final IntSetting range = add(new IntSetting("Range", "Max distance in blocks", 64, 4, 256));
    private final ColorSetting color = add(new ColorSetting("Color", "Overlay color", new Color(120, 90, 255)));

    public Chams() {
        super("Chams", "Filled entity overlay", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
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
            double[] bounds = Render3DUtil.projectBox(Render3DUtil.interpolatedBox(living));
            if (bounds == null) {
                continue;
            }
            double x = bounds[0];
            double y = bounds[1];
            double w = bounds[2] - bounds[0];
            double h = bounds[3] - bounds[1];
            Color base = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            int alpha = opacity.get();
            Render2DUtil.gradientRect(context, x, y, w, h,
                    base.withAlpha(alpha), base.withAlpha(Math.max(0, alpha - 70)));
            if (outline.get()) {
                Render2DUtil.outline(context, x, y, w, h,
                        base.withAlpha(Math.min(255, alpha + 100)), width.get());
            }
        }
    }
}
