package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/**
 * Lets the player keep moving while a GUI (e.g. inventory) is open by mirroring
 * the physical key state onto the movement bindings. Uses the default key codes,
 * which covers the standard control layout.
 */
public final class InventoryMove extends Module {

    public InventoryMove() {
        super("Inventory Move", "Move while screens are open", Category.MISC);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.POST || !inGame()) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen) {
            return;
        }
        sync(mc.options.forwardKey);
        sync(mc.options.backKey);
        sync(mc.options.leftKey);
        sync(mc.options.rightKey);
        sync(mc.options.jumpKey);
        sync(mc.options.sneakKey);
    }

    private void sync(KeyBinding binding) {
        int code = binding.getDefaultKey().getCode();
        binding.setPressed(InputUtil.isKeyPressed(mc.getWindow(), code));
    }
}
