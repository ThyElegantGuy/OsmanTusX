package com.osmantusx.module.misc;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

/** Adds or removes the player under the crosshair as a friend on middle-click. */
public final class MiddleClickFriend extends Module {

    private boolean wasPressed;

    public MiddleClickFriend() {
        super("Middle Click Friend", "Friend players with middle-click", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        boolean pressed = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(),
                GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
        if (pressed && !wasPressed && mc.targetedEntity instanceof PlayerEntity target) {
            String name = target.getGameProfile().name();
            if (OsmanTusX.FRIENDS.isFriend(name)) {
                OsmanTusX.FRIENDS.remove(name);
                OsmanTusX.NOTIFICATIONS.info("Removed friend " + name);
            } else {
                OsmanTusX.FRIENDS.add(name);
                OsmanTusX.NOTIFICATIONS.info("Added friend " + name);
            }
        }
        wasPressed = pressed;
    }
}
