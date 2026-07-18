package com.osmantusx.mixin;

import com.osmantusx.module.misc.Timer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Scales the number of ticks per frame for the Timer module. */
@Mixin(RenderTickCounter.Dynamic.class)
public class RenderTickCounterMixin {

    @Inject(method = "beginRenderTick(JZ)I", at = @At("RETURN"), cancellable = true)
    private void osman$timer(long timeMillis, boolean tick, CallbackInfoReturnable<Integer> cir) {
        float multiplier = Timer.multiplier();
        if (multiplier != 1.0f) {
            cir.setReturnValue(Math.round(cir.getReturnValueI() * multiplier));
        }
    }
}
