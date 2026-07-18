package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

/** Adds a steady forward boost while gliding with an elytra and holding jump. */
public final class ElytraHelper extends Module {

    private final DoubleSetting boost = add(new DoubleSetting("Boost", "Forward boost", 0.1, 0.02, 0.5, 0.01));

    public ElytraHelper() {
        super("Elytra Helper", "Assists elytra gliding", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        if (player().isOnGround()) {
            return;
        }
        if (player().getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA) {
            return;
        }
        if (!mc.options.jumpKey.isPressed()) {
            return;
        }
        float yaw = (float) Math.toRadians(player().getYaw());
        float pitch = (float) Math.toRadians(player().getPitch());
        double factor = boost.get();
        Vec3d velocity = player().getVelocity();
        player().setVelocity(velocity.add(
                -Math.sin(yaw) * Math.cos(pitch) * factor,
                -Math.sin(pitch) * factor,
                Math.cos(yaw) * Math.cos(pitch) * factor));
    }
}
