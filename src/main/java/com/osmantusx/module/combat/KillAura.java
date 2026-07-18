package com.osmantusx.module.combat;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.BooleanSetting;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

/**
 * Attacks the closest valid living entity within range once the vanilla attack
 * cooldown has recharged, optionally aiming the player at the target first.
 * Intended for singleplayer/consenting-server practice.
 */
public final class KillAura extends Module {

    private final DoubleSetting range = add(new DoubleSetting("Range", "Attack reach", 4.0, 2.0, 6.0, 0.1));
    private final BooleanSetting rotate = add(new BooleanSetting("Rotate", "Aim at the target", true));
    private final BooleanSetting players = add(new BooleanSetting("Players", "Target players", true));
    private final BooleanSetting mobs = add(new BooleanSetting("Mobs", "Target hostile mobs", true));

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().getAttackCooldownProgress(0.0f) < 1.0f) {
            return;
        }
        LivingEntity target = findTarget();
        if (target == null) {
            return;
        }
        if (rotate.get()) {
            OsmanTusX.ROTATIONS.lookAt(target.getEyePos());
        }
        mc.interactionManager.attackEntity(player(), target);
        player().swingHand(Hand.MAIN_HAND);
    }

    private LivingEntity findTarget() {
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity entity : world().getEntities()) {
            if (!(entity instanceof LivingEntity living) || living == player() || living.isDead()) {
                continue;
            }
            if (living instanceof PlayerEntity && (!players.get() || OsmanTusX.FRIENDS.isFriend(living.getName().getString()))) {
                continue;
            }
            if (living instanceof Monster && !mobs.get()) {
                continue;
            }
            double dist = player().distanceTo(living);
            if (dist <= range.get() && dist < bestDist) {
                bestDist = dist;
                best = living;
            }
        }
        return best;
    }
}
