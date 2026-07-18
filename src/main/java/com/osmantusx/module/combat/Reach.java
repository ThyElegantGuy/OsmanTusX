package com.osmantusx.module.combat;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.DoubleSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;

/**
 * Increases the player's entity interaction range through the vanilla attribute
 * system, restoring the default when disabled.
 */
public final class Reach extends Module {

    private static final double DEFAULT_RANGE = 3.0;

    private final DoubleSetting range = add(new DoubleSetting("Range", "Interaction range", 4.5, 3.0, 6.0, 0.1));

    public Reach() {
        super("Reach", "Extends entity interaction range", Category.COMBAT);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.phase() != TickEvent.Phase.PRE || !inGame()) {
            return;
        }
        EntityAttributeInstance instance = player().getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        if (instance != null) {
            instance.setBaseValue(range.get());
        }
    }

    @Override
    protected void onDisable() {
        if (player() != null) {
            EntityAttributeInstance instance = player().getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
            if (instance != null) {
                instance.setBaseValue(DEFAULT_RANGE);
            }
        }
    }
}
