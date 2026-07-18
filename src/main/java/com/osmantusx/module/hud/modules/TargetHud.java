package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/** Shows information about the entity currently under the crosshair. */
public final class TargetHud extends HudModule {

    private static final double WIDTH = 120;
    private static final double HEIGHT = 34;

    public TargetHud() {
        super("Target HUD", "Shows the targeted entity", 0.40, 0.35);
    }

    private LivingEntity currentTarget() {
        if (mc.crosshairTarget instanceof EntityHitResult hit
                && hit.getType() == HitResult.Type.ENTITY) {
            Entity entity = hit.getEntity();
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        LivingEntity target = currentTarget();
        if (target == null) {
            return;
        }
        Render2DUtil.roundedRect(context, x, y, WIDTH, HEIGHT, 3, new Color(18, 20, 28, 210));
        Render2DUtil.text(context, target.getName().getString(), x + 6, y + 5, OsmanTusX.THEMES.getActive().text());

        float healthPercent = Math.max(0, target.getHealth() / target.getMaxHealth());
        double barWidth = WIDTH - 12;
        Render2DUtil.rect(context, x + 6, y + 20, barWidth, 6, new Color(40, 44, 54));
        Render2DUtil.rect(context, x + 6, y + 20, barWidth * healthPercent, 6,
                healthPercent > 0.5 ? Color.GREEN : (healthPercent > 0.25 ? new Color(255, 190, 60) : Color.RED));
        Render2DUtil.text(context, String.format("%.1f HP", target.getHealth()), x + 6, y + 27,
                OsmanTusX.THEMES.getActive().textDim());
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }
}
