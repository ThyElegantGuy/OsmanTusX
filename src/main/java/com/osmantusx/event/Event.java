package com.osmantusx.event;

/**
 * Base type for every event dispatched through the {@link EventBus}.
 *
 * <p>Events default to non-cancellable. Cancellable events should override
 * {@link #isCancellable()} to return {@code true}; listeners can then call
 * {@link #cancel()} to suppress the underlying vanilla behaviour.</p>
 */
public abstract class Event {

    private boolean cancelled;

    /** @return whether this event supports cancellation. */
    public boolean isCancellable() {
        return false;
    }

    /** Marks the event as cancelled (no-op if the event is not cancellable). */
    public void cancel() {
        if (isCancellable()) {
            this.cancelled = true;
        }
    }

    /** @return {@code true} if a listener has cancelled this event. */
    public boolean isCancelled() {
        return cancelled;
    }
}
