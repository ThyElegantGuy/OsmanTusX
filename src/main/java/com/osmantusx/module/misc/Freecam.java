package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.util.math.Vec3d;

/**
 * Detaches a free-flying camera from the player. The camera position is tracked
 * here and consumed by {@code CameraMixin} (via {@link #isActive()} and
 * {@link #cameraPos()}); the player stays put while enabled.
 */
public final class Freecam extends Module {

    private static Freecam instance;

    private final DoubleSetting speed = add(new DoubleSetting("Speed", "Camera speed", 0.5, 0.1, 2.0, 0.1));

    private Vec3d cameraPos = Vec3d.ZERO;
    private float yaw;
    private float pitch;

    public Freecam() {
        super("Freecam", "Free-flying detached camera", Category.MISC);
        instance = this;
    }

    @Override
    protected void onEnable() {
        if (inGame()) {
            cameraPos = player().getEntityPos().add(0, player().getStandingEyeHeight(), 0);
            yaw = player().getYaw();
            pitch = player().getPitch();
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        yaw = player().getYaw();
        pitch = player().getPitch();
        double s = speed.get();
        Vec3d move = Vec3d.ZERO;
        float rad = (float) Math.toRadians(yaw);
        if (mc.options.forwardKey.isPressed()) {
            move = move.add(-Math.sin(rad), 0, Math.cos(rad));
        }
        if (mc.options.backKey.isPressed()) {
            move = move.add(Math.sin(rad), 0, -Math.cos(rad));
        }
        if (mc.options.leftKey.isPressed()) {
            move = move.add(Math.cos(rad), 0, Math.sin(rad));
        }
        if (mc.options.rightKey.isPressed()) {
            move = move.add(-Math.cos(rad), 0, -Math.sin(rad));
        }
        if (mc.options.jumpKey.isPressed()) {
            move = move.add(0, 1, 0);
        }
        if (mc.options.sneakKey.isPressed()) {
            move = move.add(0, -1, 0);
        }
        cameraPos = cameraPos.add(move.multiply(s));
    }

    public static boolean isActive() {
        return instance != null && instance.isEnabled();
    }

    public static Vec3d cameraPos() {
        return instance != null ? instance.cameraPos : Vec3d.ZERO;
    }

    public static float cameraYaw() {
        return instance != null ? instance.yaw : 0f;
    }

    public static float cameraPitch() {
        return instance != null ? instance.pitch : 0f;
    }
}
