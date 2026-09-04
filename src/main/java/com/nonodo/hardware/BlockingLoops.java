package com.nonodo.hardware;

import java.util.function.BooleanSupplier;

/**
 * Helpers for blocking {@code driveFor} / {@code turnBy} loops on any FTC thread,
 * including the Blocks Java Bridge thread.
 */
public final class BlockingLoops {

    private static final long LOOP_SLEEP_MS = 5;

    private BlockingLoops() {
    }

    public static void driveFor(
            double power,
            long timeMs,
            BooleanSupplier isActive,
            DriveTick driveTick,
            Runnable stop
    ) {
        if (timeMs <= 0) {
            stop.run();
            return;
        }
        long deadlineMs = System.currentTimeMillis() + timeMs;
        try {
            while (isActive.getAsBoolean() && System.currentTimeMillis() < deadlineMs) {
                driveTick.tick(power);
                yield();
            }
        } finally {
            stop.run();
        }
    }

    public static void waitFor(long timeMs, BooleanSupplier isActive) {
        if (timeMs <= 0) {
            return;
        }
        long deadlineMs = System.currentTimeMillis() + timeMs;
        while (isActive.getAsBoolean() && System.currentTimeMillis() < deadlineMs) {
            yield();
        }
    }

    public static void yield() {
        Thread.yield();
        try {
            Thread.sleep(LOOP_SLEEP_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    interface DriveTick {
        void tick(double power);
    }
}
