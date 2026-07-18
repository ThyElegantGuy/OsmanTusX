package com.osmantusx.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

/**
 * Convenience accessors shared by nearly every part of the client.
 *
 * <p>Implementing this interface gives a class terse access to the running
 * {@link MinecraftClient} instance and the most frequently used game objects
 * without repeating {@code MinecraftClient.getInstance()} everywhere.</p>
 */
public interface Wrapper {

    /** The singleton Minecraft client instance. */
    MinecraftClient mc = MinecraftClient.getInstance();

    /** @return the local player, or {@code null} when not in a world. */
    default ClientPlayerEntity player() {
        return mc.player;
    }

    /** @return the client world, or {@code null} when not in a world. */
    default ClientWorld world() {
        return mc.world;
    }

    /** @return {@code true} when the player and world are both present. */
    default boolean inGame() {
        return mc.player != null && mc.world != null;
    }
}
