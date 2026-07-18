package com.osmantusx.module.misc;

import com.mojang.authlib.GameProfile;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;

import java.util.UUID;

/**
 * Spawns a client-side fake player at your location for positioning and combat
 * practice in single-player. The entity exists only on the client.
 */
public final class FakePlayer extends Module {

    private static final int FAKE_ID = -6969;

    private OtherClientPlayerEntity fake;

    public FakePlayer() {
        super("Fake Player", "Spawns a practice dummy", Category.MISC);
    }

    @Override
    protected void onEnable() {
        if (!inGame()) {
            return;
        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), "FakePlayer");
        fake = new OtherClientPlayerEntity(world(), profile);
        fake.setId(FAKE_ID);
        fake.refreshPositionAndAngles(player().getX(), player().getY(), player().getZ(),
                player().getYaw(), player().getPitch());
        world().addEntity(fake);
    }

    @Override
    protected void onDisable() {
        if (fake != null && world() != null) {
            world().removeEntity(fake.getId(), Entity.RemovalReason.DISCARDED);
            fake = null;
        }
    }
}
