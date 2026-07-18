package com.osmantusx.module.combat;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.PacketEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

/**
 * Reduces or cancels knockback by dropping the server's velocity update packets
 * addressed to the local player.
 */
public final class Velocity extends Module {

    private final DoubleSetting horizontal =
            add(new DoubleSetting("Horizontal", "Knockback taken (%)", 0.0, 0.0, 100.0, 1.0));

    public Velocity() {
        super("Velocity", "Modifies received knockback", Category.COMBAT);
    }

    @EventHandler
    public void onPacket(PacketEvent.Receive event) {
        if (!inGame()) {
            return;
        }
        if (event.packet() instanceof EntityVelocityUpdateS2CPacket packet
                && packet.getEntityId() == player().getId()
                && horizontal.get() <= 0.0) {
            event.cancel();
        }
    }
}
