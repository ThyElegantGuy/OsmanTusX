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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Fills a translucent, always-visible overlay over living entities so they show
 * through walls (a projection-based take on classic chams).
 *
 * <p>The overlay is a soft top-down gradient with a thin outline rather than a
 * single flat rectangle, which reads as a highlight on the entity instead of a
 * hard, aliased block.</p>
 */
public final class Chams extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Overlay players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Overlay mobs", true));
    private final BooleanSetting outline = add(new BooleanSetting("Outline", "Draw a border", true));
    private final IntSetting width = add(new IntSetting("Width", "Outline width", 1, 1, 5));
    private final IntSetting opacity = add(new IntSetting("Opacity", "Fill opacity", 110, 20, 220));
    private final ColorSetting color = add(new ColorSetting("Color", "Overlay color", new Color(120, 90, 255)));

    public Chams() {
        super("Chams", "See-through entity overlay", Category.RENDER);
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
            if ((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
                continue;
            }
            double[] bounds = Esp.projectBox(living.getBoundingBox());
            if (bounds == null) {
                continue;
            }
            double x = bounds[0];
            double y = bounds[1];
            double w = bounds[2] - bounds[0];
            double h = bounds[3] - bounds[1];
            if (w < 1 || h < 1) {
                continue;
            }
            Color base = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            int alpha = opacity.get();
            Render2DUtil.gradientRect(context, x, y, w, h,
                    base.withAlpha(alpha), base.withAlpha(Math.max(0, alpha - 70)));
            if (outline.get()) {
                Render2DUtil.outline(context, x, y, w, h, base.withAlpha(Math.min(255, alpha + 100)), width.get());
            }
        }
    }
}
