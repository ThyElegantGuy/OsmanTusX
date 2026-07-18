package com.osmantusx.util.math;

/** Lightweight millisecond stopwatch used for cooldowns and animations. */
public final class Timer {

    private long lastReset = System.currentTimeMillis();

    /** @return milliseconds elapsed since the last reset. */
    public long elapsed() {
        return System.currentTimeMillis() - lastReset;
    }

    /** @return {@code true} if at least {@code millis} have passed. */
    public boolean hasElapsed(long millis) {
        return elapsed() >= millis;
    }

    /** Resets the stopwatch to now. */
    public void reset() {
        lastReset = System.currentTimeMillis();
    }

    /** Resets only if the interval has elapsed; useful for rate limiting. */
    public boolean tryReset(long millis) {
        if (hasElapsed(millis)) {
            reset();
            return true;
        }
        return false;
    }
}
