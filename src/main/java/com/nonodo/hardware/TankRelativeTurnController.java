package com.nonodo.hardware;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Robot-relative PD pivot turn for tank. Gains come from
 * {@link NODOTankDrive#getTurnPD()} at {@link #start(double)}.
 */
public class TankRelativeTurnController {

    private static final double WRAP_STICKY_DEG = 179.0;

    private final NODOTankDrive drive;
    private final TurnPDGains gains = new TurnPDGains();

    private double relativeTurnDegrees;
    private double absoluteTargetDegrees;
    private final ElapsedTime settleTimer = new ElapsedTime();

    private double error;
    private double commandedPower;
    private double lastErrorSign = 1.0;
    private boolean inTolerance;

    public TankRelativeTurnController(NODOTankDrive drive) {
        this.drive = drive;
        this.gains.copyFrom(drive.getTurnPD());
    }

    public void start(double relativeDegrees) {
        gains.copyFrom(drive.getTurnPD());
        this.relativeTurnDegrees = relativeDegrees;
        commandedPower = 0.0;
        lastErrorSign = 1.0;
        absoluteTargetDegrees = AngleMath.normalize(drive.getRawHeading() + relativeDegrees);
        refreshError();
        if (Math.abs(error) > 1e-3) {
            lastErrorSign = Math.signum(error);
        }
        settleTimer.reset();
    }

    /**
     * @return {@code true} when the turn has settled within tolerance
     */
    public boolean update() {
        refreshError();

        inTolerance = Math.abs(error) < gains.getToleranceDegrees();
        if (inTolerance) {
            commandedPower = 0.0;
            drive.setPowers(0, 0, false);
            return isFinished();
        }

        settleTimer.reset();

        double pTerm = error * gains.getKP();
        double dTerm = -gains.getKD() * drive.getYawVelocityDegreesPerSec();
        commandedPower = clamp(pTerm + dTerm, -gains.getMaxPower(), gains.getMaxPower());

        drive.setPowers(-commandedPower, commandedPower, false);
        return false;
    }

    public boolean isFinished() {
        return inTolerance && settleTimer.milliseconds() >= gains.getSettleMs();
    }

    public void end() {
        commandedPower = 0.0;
        drive.stop();
    }

    public TurnPDGains getGains() {
        return gains;
    }

    public double getRelativeTurnDegrees() {
        return relativeTurnDegrees;
    }

    public double getError() {
        return error;
    }

    public double getCommandedPower() {
        return commandedPower;
    }

    private void refreshError() {
        error = shortestPathError(absoluteTargetDegrees, drive.getRawHeading());
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
