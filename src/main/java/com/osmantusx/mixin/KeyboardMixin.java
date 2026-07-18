package com.osmantusx.mixin;

import com.osmantusx.OsmanTusX;
import com.osmantusx.event.events.KeyEvent;
import com.osmantusx.gui.ClickGuiScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Bridges GLFW key events into the internal event bus and keybind handling. */
@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void osman$onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (window != mc.getWindow().getHandle()) {
            return;
        }
        int key = input.key();
        OsmanTusX.EVENT_BUS.post(new KeyEvent(key, action));

        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || mc.currentScreen != null) {
            return;
        }
        if (key == ClickGuiScreen.openKey()) {
            mc.setScreen(new ClickGuiScreen());
            // Consume this key event so vanilla does not forward the same press
            // to the freshly opened screen (which would immediately close it).
            ci.cancel();
        } else {
            OsmanTusX.MODULES.onKeyPress(key);
        }
    }
}
