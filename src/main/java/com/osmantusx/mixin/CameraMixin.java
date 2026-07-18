package com.osmantusx.mixin;

import com.osmantusx.module.misc.Freecam;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Overrides the camera transform while Freecam is active. */
@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setPos(Vec3d pos);

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void osman$freecam(World area, Entity focusedEntity, boolean thirdPerson,
                              boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (Freecam.isActive()) {
            setRotation(Freecam.cameraYaw(), Freecam.cameraPitch());
            setPos(Freecam.cameraPos());
        }
    }
}
