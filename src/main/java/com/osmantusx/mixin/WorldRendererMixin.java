package com.osmantusx.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.osmantusx.util.render.Render3DUtil;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.memory.ObjectAllocator;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Captures the exact model-view and projection matrices the world is rendered
 * with each frame so 2D ESP/Tracers can project world positions to the screen
 * accurately (the same technique Meteor Client uses).
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void osman$captureMatrices(ObjectAllocator allocator, RenderTickCounter tickCounter,
                                       boolean renderBlockOutline, Camera camera, Matrix4f modelViewMatrix,
                                       Matrix4f positionMatrix, Matrix4f projectionMatrix, GpuBufferSlice fog,
                                       Vector4f fogColor, boolean shouldRenderSky, CallbackInfo ci) {
        Vec3d cam = camera.getCameraPos();
        Render3DUtil.updateMatrices(modelViewMatrix, projectionMatrix, cam.x, cam.y, cam.z);
    }
}
