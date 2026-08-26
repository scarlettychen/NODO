package com.nonodo.hardware;

/**
 * Tunable PD (and related) settings for robot-relative turns.
 * Defaults match the library's stock turn feel; change via
 * {@link NODOChassis#setTurnPD}, {@link NODOTankDrive#setTurnPD}, or
 * {@link NODODrive#setTurnPD}.
 */
public final class TurnPDGains {

    public static final double DEFAULT_KP = 0.035;
    public static final double DEFAULT_KD = 0.002;
    public static final double DEFAULT_MAX_POWER = 0.8;
    public static final double DEFAULT_TOLERANCE_DEG = 3.5;
    public static final double DEFAULT_SETTLE_MS = 150.0;

    private double kP = DEFAULT_KP;
    private double kD = DEFAULT_KD;
    private double maxPower = DEFAULT_MAX_POWER;
    private double toleranceDegrees = DEFAULT_TOLERANCE_DEG;
    private double settleMs = DEFAULT_SETTLE_MS;

    public double getKP() {
        return kP;
    }

    public double getKD() {
        return kD;
    }

    public double getMaxPower() {
        return maxPower;
    }

    public double getToleranceDegrees() {
        return toleranceDegrees;
    }

    public double getSettleMs() {
        return settleMs;
    }

    /** Sets proportional and derivative gains. */
    public TurnPDGains setPD(double kP, double kD) {
        this.kP = kP;
        this.kD = kD;
        return this;
    }

    public TurnPDGains setKP(double kP) {
        this.kP = kP;
        return this;
    }

    public TurnPDGains setKD(double kD) {
        this.kD = kD;
        return this;
    }

    public TurnPDGains setMaxPower(double maxPower) {
        this.maxPower = Math.max(0.0, Math.min(1.0, Math.abs(maxPower)));
        return this;
    }

    public TurnPDGains setToleranceDegrees(double toleranceDegrees) {
        this.toleranceDegrees = Math.max(0.0, Math.abs(toleranceDegrees));
        return this;
    }

    public TurnPDGains setSettleMs(double settleMs) {
        this.settleMs = Math.max(0.0, settleMs);
        return this;
    }

    public TurnPDGains copyFrom(TurnPDGains other) {
        if (other == null) {
            return this;
        }
        this.kP = other.kP;
        this.kD = other.kD;
        this.maxPower = other.maxPower;
        this.toleranceDegrees = other.toleranceDegrees;
        this.settleMs = other.settleMs;
        return this;
    }

    public TurnPDGains copy() {
        return new TurnPDGains().copyFrom(this);
    }
}
