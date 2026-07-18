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
 * 2D ESP: draws a flat outlined rectangle around each entity, projected with the
 * game's real matrices (Meteor-style) so it lines up with no delay.
 */
public final class Esp extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Highlight players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Highlight mobs", true));
    private final IntSetting width = add(new IntSetting("Width", "Box line width", 1, 1, 5));
    private final IntSetting range = add(new IntSetting("Range", "Max distance in blocks", 64, 4, 256));
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
        int maxSq = range.get() * range.get();
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
            if (player().squaredDistanceTo(living) > maxSq) {
                continue;
            }
            double[] bounds = Render3DUtil.projectBox(Render3DUtil.interpolatedBox(living));
            if (bounds == null) {
                continue;
            }
            Color c = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            Render2DUtil.outline(context, bounds[0], bounds[1], bounds[2] - bounds[0], bounds[3] - bounds[1],
                    c, width.get());
        }
    }
}
