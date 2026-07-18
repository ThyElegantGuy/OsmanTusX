package com.osmantusx.module.hud.modules;

import com.osmantusx.OsmanTusX;
import com.osmantusx.module.hud.TextHudModule;
import com.osmantusx.setting.StringSetting;

/** Displays the client name/version watermark. */
public final class WatermarkHud extends TextHudModule {

    private final StringSetting text =
            add(new StringSetting("Text", "Watermark text", OsmanTusX.NAME + " v" + OsmanTusX.VERSION));

    public WatermarkHud() {
        super("Watermark", "Shows the client watermark", 0.01, 0.01);
    }

    @Override
    protected String getText() {
        return text.get();
    }
}
