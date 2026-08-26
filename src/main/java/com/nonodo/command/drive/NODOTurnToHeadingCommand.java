package com.nonodo.command.drive;

import com.nonodo.command.NODOCommand;
import com.nonodo.hardware.NODOChassis;
import com.nonodo.hardware.RelativeTurnController;

/**
 * Robot-relative pivot turn. {@code turnDegrees} is how far to rotate from the
 * heading at {@link #init()} (positive = CCW / +yaw, negative = CW), not a
 * field absolute angle.
 *
 * <p>Optional — prefer {@link NODOChassis#turnBy} if you are not using commands.
 */
public class NODOTurnToHeadingCommand implements NODOCommand {

    private final RelativeTurnController turn;
    private final double requestedDegrees;

    public NODOTurnToHeadingCommand(NODOChassis chassis, double turnDegrees) {
        this.turn = new RelativeTurnController(chassis);
        this.requestedDegrees = turnDegrees;
    }

    @Override
    public void init() {
        turn.start(requestedDegrees);
    }

    @Override
    public void execute() {
        turn.update();
    }

    @Override
    public boolean isFinished() {
        return turn.isFinished();
    }

    @Override
    public void end() {
        turn.end();
    }

    /** Absolute IMU yaw being chased (set in init). */
    public double getTarget() {
        return turn.getTarget();
    }

    /** Requested relative turn in degrees (constructor argument). */
    public double getRelativeTurnDegrees() {
        return requestedDegrees;
    }

    public double getError() {
        return turn.getError();
    }

    public double getRawError() {
        return turn.getRawError();
    }

    public double getCommandedPower() {
        return turn.getCommandedPower();
    }

    public double getFilteredHeading() {
        return turn.getFilteredHeading();
    }

    public double getRawHeading() {
        return turn.getRawHeading();
    }

    public boolean isInTolerance() {
        return turn.isInTolerance();
    }

    public double getYawVelocityDegPerSec() {
        return turn.getYawVelocityDegPerSec();
    }

    public double getPTerm() {
        return turn.getPTerm();
    }

    public double getDTerm() {
        return turn.getDTerm();
    }
}
