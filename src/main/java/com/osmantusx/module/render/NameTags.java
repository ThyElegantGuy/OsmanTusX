package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render2DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Draws floating name/health labels above other players. */
public final class NameTags extends Module {

    public NameTags() {
        super("Name Tags", "Shows names above players", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof PlayerEntity target) || target == player()) {
                continue;
            }
            Vec3d top = target.getBoundingBox().getCenter().add(0, target.getHeight() / 2.0 + 0.4, 0);
            double[] screen = Render3DUtil.worldToScreen(top);
            if (screen == null) {
                continue;
            }
            String label = target.getName().getString() + " " + (int) target.getHealth() + "HP";
            double width = Render2DUtil.textWidth(label);
            Render2DUtil.rect(context, screen[0] - width / 2.0 - 2, screen[1] - 2, width + 4, 11,
                    new Color(18, 20, 28, 200));
            Render2DUtil.centeredText(context, label, screen[0], screen[1], OsmanTusX.THEMES.getActive().text());
        }
    }
}
