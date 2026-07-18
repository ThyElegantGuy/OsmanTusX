package com.osmantusx.module.combat;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * Automatically attacks the entity under the crosshair at a configurable CPS
 * while the attack key is held.
 */
public final class AutoClicker extends Module {

    private final DoubleSetting cps = add(new DoubleSetting("CPS", "Clicks per second", 8.0, 1.0, 20.0, 0.5));
    private long lastClick;

    public AutoClicker() {
        super("AutoClicker", "Auto-attacks while attack is held", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (!mc.options.attackKey.isPressed()) {
            return;
        }
        long interval = (long) (1000.0 / cps.get());
        long now = System.currentTimeMillis();
        if (now - lastClick < interval) {
            return;
        }
        lastClick = now;
        if (mc.crosshairTarget instanceof EntityHitResult hit && hit.getType() == HitResult.Type.ENTITY) {
            Entity entity = hit.getEntity();
            mc.interactionManager.attackEntity(player(), entity);
        }
        player().swingHand(Hand.MAIN_HAND);
    }
}
