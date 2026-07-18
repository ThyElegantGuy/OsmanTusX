package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

/** Displays how many entities are loaded in the world. */
public final class EntityCounterHud extends TextHudModule {

    public EntityCounterHud() {
        super("Entity Counter", "Shows loaded entity count", 0.01, 0.38);
    }

    @Override
    protected String getText() {
        int count = mc.world == null ? 0 : mc.world.getRegularEntityCount();
        return "Entities: " + count;
    }
}
