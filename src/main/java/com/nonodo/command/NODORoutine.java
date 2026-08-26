package com.nonodo.command;

import com.nonodo.command.drive.NODODriveStraightCommand;
import com.nonodo.command.drive.NODOStrafeCommand;
import com.nonodo.command.drive.NODOTurnToHeadingCommand;
import com.nonodo.hardware.NODOChassis;

import java.util.ArrayList;
import java.util.List;

/**
 * Optional fluent builder for time/IMU auton sequences (command framework).
 * Teams can skip this entirely and call {@link NODOChassis#driveFor},
 * {@link NODOChassis#strafeFor}, and {@link NODOChassis#turnBy} from a
 * {@code LinearOpMode} instead.
 *
 * <p>Queue steps during OpMode {@code init()}, call {@link #start()} from
 * {@code start()} so timers do not run while waiting for PLAY, then
 * {@link #loop()} each cycle (delegates to {@link NODOCommandScheduler#run()}).
 *
 * <pre>{@code
 * routine = new NODORoutine(chassis)
 *     .drive(0.65, 800)
 *     .waitMs(500)
 *     .strafe(0.5, 600)
 *     .turnTo(-90)
 *     .drive(0.8, 400);
 * // start(): routine.start();
 * // loop():  routine.loop();
 * // stop():  routine.cancelAll();
 * }</pre>
 */
public class NODORoutine {

    private final NODOChassis chassis;
    private final List<NODOCommand> steps = new ArrayList<>();
    private final NODOCommandScheduler scheduler;
    private boolean started;

    /**
     * Uses an internal scheduler owned by this routine (one per OpMode is typical).
     */
    public NODORoutine(NODOChassis chassis) {
        this(chassis, new NODOCommandScheduler());
    }

    /**
     * Share an external scheduler if the OpMode also schedules other commands.
     */
    public NODORoutine(NODOChassis chassis, NODOCommandScheduler scheduler) {
        this.chassis = chassis;
        this.scheduler = scheduler;
    }

    public NODORoutine drive(double power, int ms) {
        steps.add(new NODODriveStraightCommand(chassis, power, ms));
        return this;
    }

    public NODORoutine strafe(double power, int ms) {
        steps.add(new NODOStrafeCommand(chassis, power, ms));
        return this;
    }

    /**
     * Robot-relative turn from heading at step start (positive = CCW / +yaw).
     */
    public NODORoutine turnTo(double relativeDegrees) {
        steps.add(new NODOTurnToHeadingCommand(chassis, relativeDegrees));
        return this;
    }

    public NODORoutine waitMs(int ms) {
        steps.add(new NODOWaitCommand(ms));
        return this;
    }

    public NODORoutine then(NODOCommand customCommand) {
        if (customCommand != null) {
            steps.add(customCommand);
        }
        return this;
    }

    /**
     * Builds a sequential group of all queued steps without starting them.
     * Useful if you want to {@link NODOCommandScheduler#add} it yourself.
     */
    public NODOSequentialGroup build() {
        return new NODOSequentialGroup(steps);
    }

    /**
     * Builds the sequential group and adds it to the scheduler (calls child {@code init()}).
     * Call from OpMode {@code start()}, not {@code init()}.
     */
    public void start() {
        if (started) {
            return;
        }
        started = true;
        scheduler.add(build());
    }

    /**
     * Advances the scheduler. Call once per OpMode {@code loop()}.
     */
    public void loop() {
        scheduler.run();
    }

    /**
     * Ends and clears scheduled work. Call from OpMode {@code stop()}.
     */
    public void cancelAll() {
        scheduler.cancelAll();
        started = false;
    }

    public boolean isFinished() {
        return started && scheduler.isIdle();
    }

    public NODOCommandScheduler getScheduler() {
        return scheduler;
    }

    public NODOCommand getCurrentStep() {
        NODOCommand root = scheduler.getActiveCommand();
        if (root instanceof NODOSequentialGroup) {
            return ((NODOSequentialGroup) root).getCurrentCommand();
        }
        return root;
    }

    public int getStepIndex() {
        NODOCommand root = scheduler.getActiveCommand();
        if (root instanceof NODOSequentialGroup) {
            return ((NODOSequentialGroup) root).getIndex();
        }
        return -1;
    }

    public NODOTurnToHeadingCommand getActiveTurn() {
        NODOCommand current = getCurrentStep();
        if (current instanceof NODOTurnToHeadingCommand) {
            return (NODOTurnToHeadingCommand) current;
        }
        return null;
    }
}
