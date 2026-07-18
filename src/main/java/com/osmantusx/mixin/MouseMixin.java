package com.osmantusx.mixin;

import com.osmantusx.util.ClickTracker;
import net.minecraft.client.Mouse;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Feeds mouse-button presses into the CPS click tracker. */
@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void osman$onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (window != mc.getWindow().getHandle() || action != GLFW.GLFW_PRESS || mc.player == null) {
            return;
        }
        if (input.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            ClickTracker.onLeftClick();
        } else if (input.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            ClickTracker.onRightClick();
        }
    }
}
