package com.osmantusx.util;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Tracks mouse click timestamps to compute clicks-per-second for the CPS HUD.
 * Fed by the mouse mixin; only clicks within the last second are counted.
 */
public final class ClickTracker {

    private ClickTracker() {
    }

    private static final Deque<Long> LEFT = new ArrayDeque<>();
    private static final Deque<Long> RIGHT = new ArrayDeque<>();

    public static void onLeftClick() {
        LEFT.add(System.currentTimeMillis());
    }

    public static void onRightClick() {
        RIGHT.add(System.currentTimeMillis());
    }

    public static int leftCps() {
        return count(LEFT);
    }

    public static int rightCps() {
        return count(RIGHT);
    }

    private static int count(Deque<Long> queue) {
        long cutoff = System.currentTimeMillis() - 1000;
        while (!queue.isEmpty() && queue.peekFirst() < cutoff) {
            queue.pollFirst();
        }
        return queue.size();
    }
}
