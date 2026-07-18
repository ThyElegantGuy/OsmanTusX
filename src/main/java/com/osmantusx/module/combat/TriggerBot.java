package com.osmantusx.module.combat;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/** Attacks whatever living entity the crosshair is pointed at when charged. */
public final class TriggerBot extends Module {

    public TriggerBot() {
        super("TriggerBot", "Attacks the entity under the crosshair", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().getAttackCooldownProgress(0.0f) < 1.0f) {
            return;
        }
        if (!(mc.crosshairTarget instanceof EntityHitResult hit) || hit.getType() != HitResult.Type.ENTITY) {
            return;
        }
        if (!(hit.getEntity() instanceof LivingEntity living) || living == player() || living.isDead()) {
            return;
        }
        if (living instanceof PlayerEntity && OsmanTusX.FRIENDS.isFriend(living.getName().getString())) {
            return;
        }
        mc.interactionManager.attackEntity(player(), living);
        player().swingHand(Hand.MAIN_HAND);
    }
}
