package com.osmantusx.module.misc;

import com.osmantusx.module.Category;
import com.osmantusx.module.Module;
import com.osmantusx.setting.IntSetting;

/** Narrows the field of view for a zoom effect while enabled. */
public final class Zoom extends Module {

    private final IntSetting fov = add(new IntSetting("FOV", "Zoomed field of view", 30, 5, 70));

    private int previousFov = 70;

    public Zoom() {
        super("Zoom", "Zooms the camera", Category.MISC);
    }

    @Override
    protected void onEnable() {
        previousFov = mc.options.getFov().getValue();
        mc.options.getFov().setValue(fov.get());
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.getFov().setValue(previousFov);
        }
    }
}
