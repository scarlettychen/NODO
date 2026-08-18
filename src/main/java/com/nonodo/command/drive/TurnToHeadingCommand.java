package com.nonodo.command.drive;

import com.nonodo.command.NoOdoCommand;
import com.nonodo.hardware.NoOdoChassis;

public class TurnToHeadingCommand implements NoOdoCommand {

    private static final double TURN_GAIN = 0.02;
    private static final double MAX_POWER = 0.8;
    private static final double HEADING_TOLERANCE_DEG = 2.0;

    private final NoOdoChassis chassis;
    private final double targetYawDegrees;
    private double error;

    public TurnToHeadingCommand(NoOdoChassis chassis, double targetYawDegrees) {
        this.chassis = chassis;
        this.targetYawDegrees = targetYawDegrees;
    }

    @Override
    public void init() {
        // Do not reset the IMU; this command turns to an absolute field heading.
        error = headingError();
    }

    @Override
    public void execute() {
        error = headingError();
        double power = error * TURN_GAIN;
        power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        chassis.setMecanumPowers(power, -power, power, -power);
    }

    @Override
    public boolean isFinished() {
        return Math.abs(error) < HEADING_TOLERANCE_DEG;
    }

    @Override
    public void end() {
        chassis.setMecanumPowers(0, 0, 0, 0);
    }

    private double headingError() {
        double headingError = targetYawDegrees - chassis.getYaw();
        while (headingError > 180.0) {
            headingError -= 360.0;
        }
        while (headingError < -180.0) {
            headingError += 360.0;
        }
        return headingError;
    }
}
