package com.nonodo.command.drive;

import com.nonodo.command.NoOdoCommand;
import com.nonodo.hardware.NoOdoChassis;
import com.qualcomm.robotcore.util.ElapsedTime;

public class DriveStraightCommand implements NoOdoCommand {

    private static final double HEADING_GAIN = 0.02;

    private final NoOdoChassis chassis;
    private final double power;
    private final long timeMs;
    private ElapsedTime timer;
    private double targetHeading;

    public DriveStraightCommand(NoOdoChassis chassis, double power, long timeMs) {
        this.chassis = chassis;
        this.power = power;
        this.timeMs = timeMs;
    }

    @Override
    public void init() {
        timer = new ElapsedTime();
        targetHeading = chassis.getHeading();
    }

    @Override
    public void execute() {
        double currentHeading = chassis.getHeading();
        double headingError = targetHeading - currentHeading;
        headingError = normalizeAngle(headingError);

        double correction = headingError * HEADING_GAIN;
        chassis.setMecanumPowers(
                power - correction,
                power + correction,
                power - correction,
                power + correction
        );
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeMs;
    }

    @Override
    public void end() {
        chassis.setMecanumPowers(0, 0, 0, 0);
    }

    private static double normalizeAngle(double degrees) {
        while (degrees > 180.0) {
            degrees -= 360.0;
        }
        while (degrees < -180.0) {
            degrees += 360.0;
        }
        return degrees;
    }
}
