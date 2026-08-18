package com.nonodo.samples;

import com.nonodo.hardware.NoOdoChassis;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "Sample No-Odo Auto")
public class SampleNoOdoAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        /*
         * To find your kF, slowly increase this number from 0.01 until your robot
         * just barely starts to whine or inch forward when given 0 power, then
         * back it down slightly.
         */
        NoOdoChassis chassis = new NoOdoChassis(hardwareMap, 0.03);

        waitForStart();

        if (opModeIsActive()) {
            chassis.driveStraight(0.5, 2000);
        }
    }
}
