package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

/**
 * Fills a translucent, always-visible overlay over living entities so they show
 * through walls (a projection-based take on classic chams).
 */
public final class Chams extends Module {

    public Chams() {
        super("Chams", "See-through entity overlay", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        Color fill = OsmanTusX.THEMES.accent().withAlpha(90);
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof LivingEntity living) || living == player() || living.isDead()) {
                continue;
            }
            double[] bounds = Esp.projectBox(living.getBoundingBox());
            if (bounds == null) {
                continue;
            }
            Render2DUtil.rect(context, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1], fill);
        }
    }
}
