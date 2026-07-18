package com.osmantusx.module.movement;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

/** Raises the player's step height so they can walk up full blocks. */
public final class Step extends Module {

    private static final double DEFAULT_STEP = 0.6;

    private final DoubleSetting height = add(new DoubleSetting("Height", "Step height", 1.0, 0.6, 2.0, 0.1));

    public Step() {
        super("Step", "Step up full blocks", Category.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        apply(height.get());
    }

    @Override
    protected void onDisable() {
        apply(DEFAULT_STEP);
    }

    private void apply(double value) {
        if (player() == null) {
            return;
        }
        EntityAttributeInstance instance = player().getAttributeInstance(EntityAttributes.STEP_HEIGHT);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
}
