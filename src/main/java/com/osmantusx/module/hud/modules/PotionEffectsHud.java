package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.hud.HudModule;
import com.osmantusx.util.render.Color;
import com.osmantusx.util.render.Render2DUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.Collection;

/** Lists the player's active potion effects with remaining duration. */
public final class PotionEffectsHud extends HudModule {

    public PotionEffectsHud() {
        super("Potion Effects", "Shows active status effects", 0.85, 0.05);
    }

    @Override
    public void render(DrawContext context, double x, double y) {
        if (mc.player == null) {
            return;
        }
        Collection<StatusEffectInstance> effects = mc.player.getStatusEffects();
        int line = 0;
        for (StatusEffectInstance effect : effects) {
            Render2DUtil.text(context, format(effect), x, y + line * 11, OsmanTusX.THEMES.getActive().text());
            line++;
        }
        if (effects.isEmpty()) {
            Render2DUtil.text(context, "No effects", x, y, new Color(150, 155, 170));
        }
    }

    private String format(StatusEffectInstance effect) {
        String name = effect.getEffectType().value().getName().getString();
        int amplifier = effect.getAmplifier() + 1;
        int seconds = effect.getDuration() / 20;
        return name + " " + amplifier + " (" + (seconds / 60) + ":" + String.format("%02d", seconds % 60) + ")";
    }

    @Override
    public double getWidth() {
        if (mc.player == null) {
            return 60;
        }
        double max = 60;
        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            max = Math.max(max, Render2DUtil.textWidth(format(effect)));
        }
        return max;
    }

    @Override
    public double getHeight() {
        int count = mc.player == null ? 1 : Math.max(1, mc.player.getStatusEffects().size());
        return count * 11;
    }
}
