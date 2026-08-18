package com.nonodo.command.drive;

import com.nonodo.command.NoOdoCommand;
import com.nonodo.hardware.NoOdoChassis;
import com.qualcomm.robotcore.util.ElapsedTime;

public class StrafeCommand implements NoOdoCommand {

    private static final double HEADING_GAIN = 0.02;

    private final NoOdoChassis chassis;
    private final double power;
    private final long timeMs;
    private ElapsedTime timer;

    // Positive power strafes right; negative power strafes left.
    public StrafeCommand(NoOdoChassis chassis, double power, long timeMs) {
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
