package com.nonodo.command.drive;

import com.nonodo.command.NODOCommand;
import com.nonodo.hardware.NODOTankDrive;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Tank timed drive with gyro heading hold.
 * Optional — prefer {@link NODOTankDrive#driveFor} if you are not using commands.
 */
public class NODOTankDriveStraightCommand implements NODOCommand {

    private final NODOTankDrive drive;
    private final double power;
    private final long timeMs;
    private ElapsedTime timer;
    private double targetHeading;

    public NODOTankDriveStraightCommand(NODOTankDrive drive, double power, long timeMs) {
        this.drive = drive;
        this.power = power;
        this.timeMs = timeMs;
    }

    @Override
    public void init() {
        timer = new ElapsedTime();
        targetHeading = drive.getRawHeading();
    }

    @Override
    public void execute() {
        drive.applyDriveHold(power, targetHeading);
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeMs;
    }

    @Override
    public void end() {
        drive.stop();
    }
}
