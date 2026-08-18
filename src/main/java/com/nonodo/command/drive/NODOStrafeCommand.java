package com.nonodo.command.drive;

import com.nonodo.command.NODOCommand;
import com.nonodo.hardware.NODOChassis;
import com.qualcomm.robotcore.util.ElapsedTime;

public class NODOStrafeCommand implements NODOCommand {

    private static final double HEADING_GAIN = 0.02;

    private final NODOChassis chassis;
    private final double power;
    private final long timeMs;
    private ElapsedTime timer;

    // Positive power strafes right; negative power strafes left.
    public NODOStrafeCommand(NODOChassis chassis, double power, long timeMs) {
        this.chassis = chassis;
        this.power = power;
        this.timeMs = timeMs;
    }

    @Override
    public void init() {
        timer = new ElapsedTime();
        chassis.resetYaw();
    }

    @Override
    public void execute() {
        double yaw = chassis.getYaw();
        double correction = yaw * HEADING_GAIN;

        double fl = power + correction;
        double fr = -power - correction;
        double bl = -power + correction;
        double br = power - correction;

        chassis.setMecanumPowers(fl, fr, bl, br);
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeMs;
    }

    @Override
    public void end() {
        chassis.setMecanumPowers(0, 0, 0, 0);
    }
}
