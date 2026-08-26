package com.nonodo.command;

import com.nonodo.command.drive.NODOTankDriveStraightCommand;
import com.nonodo.command.drive.NODOTankTurnCommand;
import com.nonodo.hardware.NODOTankDrive;

import java.util.ArrayList;
import java.util.List;

/**
 * Optional fluent auto builder for tank (command framework), matching
 * {@link NODORoutine} except no strafe. Teams can skip this and use
 * {@link com.nonodo.hardware.NODOTankDrive#driveFor} /
 * {@link com.nonodo.hardware.NODOTankDrive#turnBy} instead.
 *
 * <p>Queue in {@code init()}, {@link #start()} on PLAY, {@link #loop()} each cycle.
 */
public class NODOTankRoutine {

    private final NODOTankDrive drive;
    private final List<NODOCommand> steps = new ArrayList<>();
    private final NODOCommandScheduler scheduler = new NODOCommandScheduler();
    private boolean started;

    public NODOTankRoutine(NODOTankDrive drive) {
        this.drive = drive;
    }

    public NODOTankRoutine drive(double power, long timeMs) {
        steps.add(new NODOTankDriveStraightCommand(drive, power, timeMs));
        return this;
    }

    /**
     * Robot-relative turn from heading at step start (positive = CCW / +yaw).
     */
    public NODOTankRoutine turnTo(double degrees) {
        steps.add(new NODOTankTurnCommand(drive, degrees));
        return this;
    }

    public NODOTankRoutine waitMs(long timeMs) {
        steps.add(new NODOWaitCommand(timeMs));
        return this;
    }

    public NODOTankRoutine then(NODOCommand command) {
        steps.add(command);
        return this;
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;
        scheduler.add(new NODOSequentialGroup(steps.toArray(new NODOCommand[0])));
    }

    public void loop() {
        scheduler.run();
    }

    public void cancelAll() {
        scheduler.cancelAll();
        started = false;
    }

    public boolean isFinished() {
        return started && scheduler.isIdle();
    }
}
