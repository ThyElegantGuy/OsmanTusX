package com.osmantusx.mixin;

import com.osmantusx.module.misc.FastPlace;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Zeroes the item-use cooldown for the Fast Place module. */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    private int itemUseCooldown;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void osman$fastPlace(CallbackInfo ci) {
        if (FastPlace.isActive()) {
            this.itemUseCooldown = 0;
        }
    }
}
