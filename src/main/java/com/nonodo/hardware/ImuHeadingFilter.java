package com.nonodo.hardware;

/**
 * Wrap-aware exponential low-pass on IMU yaw (degrees).
 * Filters the shortest angular step so ±180 wrapping does not spike the estimate.
 */
public class ImuHeadingFilter {

    /**
     * Weight on the previous estimate. Higher = smoother / more lag.
     * 0.8 matches {@link BatteryVoltageFilter} and is a good FTC loop default.
     */
    public static final double ALPHA = 0.5;

    private double filteredDegrees;
    private boolean seeded;

    public void reset() {
        seeded = false;
        filteredDegrees = 0.0;
    }

    public void resetTo(double degrees) {
        filteredDegrees = normalize(degrees);
        seeded = true;
    }

    /**
     * Incorporate a new raw IMU yaw sample and return the filtered heading.
     */
    public double update(double rawDegrees) {
        double raw = normalize(rawDegrees);
        if (!seeded) {
            filteredDegrees = raw;
            seeded = true;
            return filteredDegrees;
        }
        double step = normalize(raw - filteredDegrees);
        filteredDegrees = normalize(filteredDegrees + (1.0 - ALPHA) * step);
        return filteredDegrees;
    }

    public double getHeading() {
        return filteredDegrees;
    }

    public boolean isSeeded() {
        return seeded;
    }

    private static double normalize(double degrees) {
        while (degrees > 180.0) {
            degrees -= 360.0;
        }
        while (degrees < -180.0) {
            degrees += 360.0;
        }
        return degrees;
    }
}
