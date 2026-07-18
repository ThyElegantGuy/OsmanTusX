package com.osmantusx.mixin;

import com.osmantusx.module.render.XRay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * X-Ray: forces non-ore block faces not to render so terrain becomes
 * transparent and only the ores stay visible.
 */
@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void osman$xray(BlockState state, BlockState otherState, Direction side,
                                   CallbackInfoReturnable<Boolean> cir) {
        if (XRay.isActive()) {
            cir.setReturnValue(XRay.isVisible(state));
        }
    }
}
