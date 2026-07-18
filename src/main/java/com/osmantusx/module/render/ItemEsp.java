package com.osmantusx.module.render;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

/** Highlights dropped item entities with a coloured box. */
public final class ItemEsp extends Module {

    private static final Color COLOR = new Color(255, 210, 60);

    public ItemEsp() {
        super("Item ESP", "Highlights dropped items", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof ItemEntity item)) {
                continue;
            }
            double[] bounds = Render3DUtil.projectBox(item.getBoundingBox());
            if (bounds == null) {
                continue;
            }
            Render2DUtil.outline(context, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1], COLOR);
        }
    }
}
