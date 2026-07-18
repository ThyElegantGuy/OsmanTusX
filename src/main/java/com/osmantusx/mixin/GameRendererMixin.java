package com.osmantusx.mixin;

import com.osmantusx.module.render.NoHurtCam;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cancels the damage camera tilt when No Hurt Cam is active. */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void osman$noHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (NoHurtCam.isActive()) {
            ci.cancel();
        }
    }
}
