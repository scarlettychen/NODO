package com.nonodo.hardware;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Robot-relative PD pivot turn for mecanum. Gains come from
 * {@link NODOChassis#getTurnPD()} at {@link #start(double)}.
 */
public class RelativeTurnController {

    private static final double WRAP_STICKY_DEG = 179.0;

    private final NODOChassis chassis;
    private final TurnPDGains gains = new TurnPDGains();

    private double relativeTurnDegrees;
    private double absoluteTargetDegrees;
    private final ElapsedTime settleTimer = new ElapsedTime();

    private double error;
    private double rawError;
    private double commandedPower;
    private double pTerm;
    private double dTerm;
    private double yawVelocityDegPerSec;
    private double filteredHeading;
    private double rawHeading;
    private boolean inTolerance;
    private double lastErrorSign = 1.0;

    public RelativeTurnController(NODOChassis chassis) {
        this.chassis = chassis;
        this.gains.copyFrom(chassis.getTurnPD());
    }

    public void start(double relativeDegrees) {
        gains.copyFrom(chassis.getTurnPD());
        this.relativeTurnDegrees = relativeDegrees;
        commandedPower = 0.0;
        pTerm = 0.0;
        dTerm = 0.0;
        yawVelocityDegPerSec = 0.0;
        lastErrorSign = 1.0;

        absoluteTargetDegrees = AngleMath.normalize(chassis.getRawHeading() + relativeDegrees);

        refreshState();
        if (Math.abs(error) > 1e-3) {
            lastErrorSign = Math.signum(error);
        }
        settleTimer.reset();
    }

    /**
     * @return {@code true} when the turn has settled within tolerance
     */
    public boolean update() {
        refreshState();

        inTolerance = Math.abs(error) < gains.getToleranceDegrees();
        if (inTolerance) {
            commandedPower = 0.0;
            pTerm = 0.0;
            dTerm = 0.0;
            chassis.setMecanumPowers(0, 0, 0, 0, false);
            return isFinished();
        }

        settleTimer.reset();

        pTerm = error * gains.getKP();
        dTerm = -gains.getKD() * yawVelocityDegPerSec;
        commandedPower = clamp(pTerm + dTerm, -gains.getMaxPower(), gains.getMaxPower());

        chassis.setMecanumPowers(
                -commandedPower, commandedPower,
                -commandedPower, commandedPower,
                false
        );
        return false;
    }

    public boolean isFinished() {
        return inTolerance && settleTimer.milliseconds() >= gains.getSettleMs();
    }

    public void end() {
        commandedPower = 0.0;
        pTerm = 0.0;
        dTerm = 0.0;
        chassis.stop();
    }

    public TurnPDGains getGains() {
        return gains;
    }

    public double getTarget() {
        return absoluteTargetDegrees;
    }

    public double getRelativeTurnDegrees() {
        return relativeTurnDegrees;
    }

    public double getError() {
        return error;
    }

    public double getRawError() {
        return rawError;
    }

    public double getCommandedPower() {
        return commandedPower;
    }

    public double getFilteredHeading() {
        return filteredHeading;
    }

    public double getRawHeading() {
        return rawHeading;
    }

    public boolean isInTolerance() {
        return inTolerance;
    }

    public double getYawVelocityDegPerSec() {
        return yawVelocityDegPerSec;
    }

    public double getPTerm() {
        return pTerm;
    }

    public double getDTerm() {
        return dTerm;
    }

    private void refreshState() {
        filteredHeading = chassis.getHeading();
        rawHeading = chassis.getRawHeading();
        yawVelocityDegPerSec = chassis.getYawVelocityDegreesPerSec();
        error = shortestPathError(absoluteTargetDegrees, rawHeading);
        rawError = error;
    }

    private double shortestPathError(double targetDeg, double currentDeg) {
        double err = AngleMath.normalize(AngleMath.normalize(targetDeg) - AngleMath.normalize(currentDeg));
        if (Math.abs(err) >= WRAP_STICKY_DEG) {
            return Math.copySign(WRAP_STICKY_DEG, lastErrorSign);
        }
        if (Math.abs(err) > 1e-3) {
            lastErrorSign = Math.signum(err);
        }
        return err;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
