package com.osmantusx.module.hud.modules;

import com.osmantusx.module.hud.TextHudModule;

/** Displays JVM heap usage. */
public final class MemoryHud extends TextHudModule {

    public MemoryHud() {
        super("Memory Usage", "Shows JVM memory usage", 0.01, 0.32);
    }

    @Override
    protected String getText() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long max = runtime.maxMemory() / (1024 * 1024);
        int percent = max == 0 ? 0 : (int) (used * 100 / max);
        return "Mem: " + used + "/" + max + "MB (" + percent + "%)";
    }
}
