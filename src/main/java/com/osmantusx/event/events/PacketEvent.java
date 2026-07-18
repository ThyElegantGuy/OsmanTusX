package com.osmantusx.event.events;

import com.osmantusx.event.Event;
import net.minecraft.network.packet.Packet;

/**
 * Packet pipeline events. {@link Send} is fired before a packet leaves the
 * client and {@link Receive} before an inbound packet is processed. Both are
 * cancellable to allow modules to drop packets (e.g. NoRotate, anti-cheat
 * friendly timers).
 */
public abstract class PacketEvent extends Event {

    private final Packet<?> packet;

    protected PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> packet() {
        return packet;
    }

    @Override
    public boolean isCancellable() {
        return true;
    }

    /** Outbound packet event. */
    public static final class Send extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    /** Inbound packet event. */
    public static final class Receive extends PacketEvent {
        public Receive(Packet<?> packet) {
            super(packet);
        }
    }
}
