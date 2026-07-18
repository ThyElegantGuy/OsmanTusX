package com.osmantusx.module.misc;

import com.osmantusx.event.EventHandler;
import com.osmantusx.event.events.TickEvent;
import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;
import com.osmantusx.util.ISimpleOption;

/** Narrows the field of view for a zoom effect while enabled. */
public final class Zoom extends Module {

    private final IntSetting fov = add(new IntSetting("FOV", "Zoomed field of view", 30, 5, 70));

    private int previousFov = 70;

    public Zoom() {
        super("Zoom", "Zooms the camera", Category.MISC);
    }

    @SuppressWarnings("unchecked")
    private void setFov(int value) {
        // Force-set bypasses the vanilla 30-110 clamp so low zoom values apply.
        ((ISimpleOption<Integer>) (Object) mc.options.getFov()).osman$forceSetValue(value);
    }

    @Override
    protected void onEnable() {
        previousFov = mc.options.getFov().getValue();
        setFov(fov.get());
    }

    @EventHandler
    public void onTick(TickEvent event) {
        // Keep the FOV in sync so adjusting the slider zooms live.
        if (event.phase() == TickEvent.Phase.PRE && mc.options.getFov().getValue() != fov.get()) {
            setFov(fov.get());
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            setFov(previousFov);
        }
    }
}
