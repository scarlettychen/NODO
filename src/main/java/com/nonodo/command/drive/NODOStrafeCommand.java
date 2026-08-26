package com.nonodo.command.drive;

import com.nonodo.command.NODOCommand;
import com.nonodo.hardware.NODOChassis;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Time-based strafe with gyro heading hold. Positive power = right, negative = left.
 * Optional — prefer {@link NODOChassis#strafeFor} if you are not using commands.
 */
public class NODOStrafeCommand implements NODOCommand {

    private final NODOChassis chassis;
    private final double power;
    private final long timeMs;
    private ElapsedTime timer;
    private double targetHeading;

    public NODOStrafeCommand(NODOChassis chassis, double power, long timeMs) {
        this.chassis = chassis;
        this.power = power;
        this.timeMs = timeMs;
    }

    @Override
    public void init() {
        timer = new ElapsedTime();
        // Lock heading at strafe start. Do not resetYaw — preserves absolute field yaw.
        targetHeading = chassis.getRawHeading();
    }

    @Override
    public void execute() {
        chassis.applyStrafeHold(power, targetHeading);
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeMs;
    }

    @Override
    public void end() {
        chassis.stop();
    }
}
