package com.osmantusx.mixin;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.events.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires the cancellable inbound {@link PacketEvent.Receive} for handled packets. */
@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
            at = @At("HEAD"), cancellable = true)
    private void osman$channelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        PacketEvent.Receive event = OsmanTusX.EVENT_BUS.post(new PacketEvent.Receive(packet));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
