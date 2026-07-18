package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;
import com.osmantusx.util.SessionTimer;

/** Displays how long the current session has been running. */
public final class SessionTimeHud extends TextHudModule {

    public SessionTimeHud() {
        super("Session Time", "Shows session uptime", 0.01, 0.26);
    }

    @Override
    protected String getText() {
        return "Session: " + SessionTimer.formatted();
    }
}
