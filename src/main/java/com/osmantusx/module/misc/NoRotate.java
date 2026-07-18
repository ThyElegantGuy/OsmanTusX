package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.PacketEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/** Suppresses rotation-only movement packets so look changes are not reported. */
public final class NoRotate extends Module {

    public NoRotate() {
        super("No Rotate", "Hides look-only rotation packets", Category.MISC);
    }

    @EventHandler
    public void onSend(PacketEvent.Send event) {
        if (event.packet() instanceof PlayerMoveC2SPacket.LookAndOnGround) {
            event.cancel();
        }
    }
}
