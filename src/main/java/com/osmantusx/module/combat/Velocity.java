package com.osmantusx.module.combat;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.PacketEvent;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.util.math.Vec3d;

/**
 * Reduces or cancels received knockback. At 0% the server's velocity packet is
 * dropped entirely; between 0% and 100% the knockback is re-applied scaled down
 * on the next client tick (so it works off the network thread safely).
 */
public final class Velocity extends Module {

    private final DoubleSetting horizontal =
            add(new DoubleSetting("Horizontal", "Knockback taken (%)", 0.0, 0.0, 100.0, 1.0));

    private Vec3d pending;

    public Velocity() {
        super("Velocity", "Modifies received knockback", Category.COMBAT);
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        if (!inGame()) {
            return;
        }
        if (!(event.packet() instanceof EntityVelocityUpdateS2CPacket packet)
                || packet.getEntityId() != player().getId()) {
            return;
        }
        double scale = horizontal.get() / 100.0;
        if (scale >= 1.0) {
            return;
        }
        if (scale > 0.0) {
            Vec3d v = packet.getVelocity();
            pending = new Vec3d(v.x * scale, v.y, v.z * scale);
        }
        event.cancel();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || pending == null || !inGame()) {
            return;
        }
        player().setVelocity(pending);
        pending = null;
    }
}
