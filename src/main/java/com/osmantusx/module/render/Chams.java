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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

/**
 * Fills a translucent see-through box over living entities in the world pass,
 * clearly distinct from ESP's plain outline.
 */
public final class Chams extends Module {

    private final BooleanSetting players = add(new BooleanSetting("Players", "Overlay players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Overlay mobs", true));
    private final BooleanSetting outline = add(new BooleanSetting("Outline", "Draw a border", true));
    private final IntSetting width = add(new IntSetting("Width", "Outline width", 2, 1, 10));
    private final IntSetting opacity = add(new IntSetting("Opacity", "Fill opacity", 90, 20, 220));
    private final IntSetting range = add(new IntSetting("Range", "Max distance in blocks", 64, 4, 256));
    private final ColorSetting color = add(new ColorSetting("Color", "Overlay color", new Color(120, 90, 255)));

    public Chams() {
        super("Chams", "See-through entity overlay", Category.RENDER);
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
            Box box = Render3DUtil.interpolatedBox(living);
            Color base = color.isRainbow() ? OsmanTusX.THEMES.rainbow(0) : color.get();
            Render3DUtil.drawFilledBox(matrices, box, base.withAlpha(opacity.get()).argb());
            if (outline.get()) {
                Render3DUtil.drawBoxOutline(matrices, box,
                        base.withAlpha(Math.min(255, opacity.get() + 120)).argb(), width.get());
            }
            drew = true;
        }
        if (drew) {
            Render3DUtil.flush();
        }
    }
}
