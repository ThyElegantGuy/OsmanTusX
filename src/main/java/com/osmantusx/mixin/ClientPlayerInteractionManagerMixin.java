package com.osmantusx.mixin;

import com.osmantusx.module.misc.FastBreak;
import com.osmantusx.module.world.FastMine;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Zeroes the block-breaking cooldown for the Fast Mine / Fast Break modules. */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow
    private int blockBreakingCooldown;

    @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"))
    private void osman$fastMine(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (FastMine.isActive() || FastBreak.isActive()) {
            this.blockBreakingCooldown = 0;
        }
    }
}
