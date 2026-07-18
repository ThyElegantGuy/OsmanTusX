package com.osmantusx.event.events;

import com.osmantusx.event.Event;

/**
 * Fired by the keyboard mixin when a physical key changes state. Cancellable so
 * modules can consume keys they handle.
 */
public final class KeyEvent extends Event {

    /** GLFW key code. */
    private final int key;
    /** GLFW action (press/release/repeat). */
    private final int action;

    public KeyEvent(int key, int action) {
        this.key = key;
        this.action = action;
    }

    public int key() {
        return key;
    }

    public int action() {
        return action;
    }

    @Override
    public boolean isCancellable() {
        return true;
    }
}
