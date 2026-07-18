package com.osmantusx.module.render;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.Render3DEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.ColorSetting;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

/** Draws crisp 3D boxes around living entities in the world render pass. */
public final class Esp extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Highlight players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Highlight mobs", true));
    private final IntSetting width = add(new IntSetting("Width", "Box line width", 2, 1, 10));
    private final IntSetting range = add(new IntSetting("Range", "Max distance in blocks", 64, 4, 256));
    private final ColorSetting color = add(new ColorSetting("Color", "Box color", new Color(120, 90, 255)));

    public Esp() {
        super("ESP", "Shows 3D boxes around entities", Category.RENDER);
    }

    @EventHandler
    public void onRender3D(Render3DEvent event) {
        if (!inGame()) {
            return;
        }
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
            Box box = Render3DUtil.interpolatedBox(living);
            Color c = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            Render3DUtil.drawBoxOutline(event.context(), box, c, width.get());
        }
    }
}
