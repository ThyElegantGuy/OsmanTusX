package com.osmantusx.util;

/** Records when the client started so the Session Time HUD can report uptime. */
public final class SessionTimer {

    private SessionTimer() {
    }

    private static final long START = System.currentTimeMillis();

    /** @return elapsed session time formatted as {@code HH:mm:ss}. */
    public static String formatted() {
        long seconds = (System.currentTimeMillis() - START) / 1000;
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
