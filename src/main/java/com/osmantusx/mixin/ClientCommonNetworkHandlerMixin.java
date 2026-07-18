package com.osmantusx.mixin;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.events.PacketEvent;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires the cancellable outbound {@link PacketEvent.Send} for every sent packet. */
@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void osman$sendPacket(Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Send event = OsmanTusX.EVENT_BUS.post(new PacketEvent.Send(packet));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
