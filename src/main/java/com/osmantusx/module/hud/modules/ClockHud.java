package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Displays the current wall-clock time. */
public final class ClockHud extends TextHudModule {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClockHud() {
        super("Clock", "Shows the current time", 0.01, 0.23);
    }

    @Override
    protected String getText() {
        return LocalTime.now().format(FORMAT);
    }
}
