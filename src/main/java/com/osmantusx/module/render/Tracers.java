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

/** Draws lines from the bottom of the screen to nearby entities. */
public final class Tracers extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Trace players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Trace mobs", true));

    public Tracers() {
        super("Tracers", "Lines pointing to entities", Category.RENDER);
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (!inGame()) {
            return;
        }
        DrawContext context = event.context();
        double originX = mc.getWindow().getScaledWidth() / 2.0;
        double originY = mc.getWindow().getScaledHeight();
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof LivingEntity living) || living == player() || living.isDead()) {
                continue;
            }
            boolean isPlayer = living instanceof PlayerEntity;
            if ((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
                continue;
            }
            double[] screen = Render3DUtil.worldToScreen(living.getBoundingBox().getCenter());
            if (screen == null) {
                continue;
            }
            Color color = isPlayer ? OsmanTusX.THEMES.accent() : Color.RED;
            Render2DUtil.line(context, originX, originY, screen[0], screen[1], color);
        }
    }
}
