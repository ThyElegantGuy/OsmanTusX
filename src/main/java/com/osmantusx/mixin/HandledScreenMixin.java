package com.osmantusx.mixin;

import com.osmantusx.module.render.ShulkerPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** Draws a shulker box's contents when hovering it, for the Shulker Preview module. */
@Mixin(net.minecraft.client.gui.screen.ingame.HandledScreen.class)
public class HandledScreenMixin {

    @Shadow
    protected Slot focusedSlot;

    @Inject(method = "render", at = @At("TAIL"))
    private void osman$shulkerPreview(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ShulkerPreview.isActive() || focusedSlot == null || !focusedSlot.hasStack()) {
            return;
        }
        ContainerComponent container = focusedSlot.getStack().get(DataComponentTypes.CONTAINER);
        if (container == null) {
            return;
        }
        List<ItemStack> items = container.stream().toList();
        if (items.isEmpty()) {
            return;
        }
        int rows = (int) Math.ceil(items.size() / 9.0);
        int width = 9 * 18 + 8;
        int height = rows * 18 + 8;
        int x = mouseX + 12;
        int y = mouseY + 12;
        context.fill(x, y, x + width, y + height, 0xF0101018);
        context.fill(x, y, x + width, y + 1, 0xFF3A7BD5);
        context.fill(x, y + height - 1, x + width, y + height, 0xFF3A7BD5);
        context.fill(x, y, x + 1, y + height, 0xFF3A7BD5);
        context.fill(x + width - 1, y, x + width, y + height, 0xFF3A7BD5);
        MinecraftClient mc = MinecraftClient.getInstance();
        for (int i = 0; i < items.size(); i++) {
            int ix = x + 4 + (i % 9) * 18;
            int iy = y + 4 + (i / 9) * 18;
            context.drawItem(items.get(i), ix, iy);
            context.drawStackOverlay(mc.textRenderer, items.get(i), ix, iy);
        }
    }
}
