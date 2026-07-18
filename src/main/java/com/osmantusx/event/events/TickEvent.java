package com.osmantusx.event.events;

import com.osmantusx.event.Event;

/**
 * Fired every client tick. {@link Phase#PRE} runs before the vanilla tick body,
 * {@link Phase#POST} after it.
 */
public final class TickEvent extends Event {

    public enum Phase {
        PRE,
        POST
    }

    private final Phase phase;

    public TickEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase phase() {
        return phase;
    }
}
