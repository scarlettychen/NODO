package com.nonodo.samples;

import com.nonodo.command.CommandScheduler;
import com.nonodo.command.SequentialGroup;
import com.nonodo.command.WaitCommand;
import com.nonodo.command.drive.DriveStraightCommand;
import com.nonodo.hardware.NoOdoChassis;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@Autonomous(name = "Sample Command Auto")
public class SampleCommandAuto extends OpMode {

    private CommandScheduler scheduler;

    @Override
    public void init() {
        NoOdoChassis chassis = new NoOdoChassis(hardwareMap, 0.03);
        scheduler = new CommandScheduler();

        SequentialGroup routine = new SequentialGroup(
                new DriveStraightCommand(chassis, 0.5, 1000),
                new WaitCommand(500),
                new DriveStraightCommand(chassis, -0.5, 1000)
        );
        scheduler.add(routine);
    }

    @Override
    public void loop() {
        /*
         * Because of the command system, they never have to use sleep()
         * or LinearOpMode ever again.
         */
        scheduler.run();
    }
}
