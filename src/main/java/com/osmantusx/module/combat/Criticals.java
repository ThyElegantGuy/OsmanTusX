package com.osmantusx.module.combat;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * Sends a minimal packet "hop" before attacks so hits register as critical
 * while the attack key is held on the ground.
 */
public final class Criticals extends Module {

    public Criticals() {
        super("Criticals", "Performs packet crits", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame() || mc.getNetworkHandler() == null) {
            return;
        }
        if (!mc.options.attackKey.isPressed() || !player().isOnGround()) {
            return;
        }
        if (player().getAttackCooldownProgress(0.0f) < 1.0f) {
            return;
        }
        double x = player().getX();
        double y = player().getY();
        double z = player().getZ();
        boolean collision = player().horizontalCollision;
        var handler = mc.getNetworkHandler();
        handler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false, collision));
        handler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, collision));
        handler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.1e-6, z, false, collision));
        handler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true, collision));
    }
}
