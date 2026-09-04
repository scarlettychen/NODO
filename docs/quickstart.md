---
layout: default
title: Quickstart
nav_order: 3
---

# Quickstart

Before you write your own autonomous paths, copy our sample OpMode to verify your hardware is working and to tune your chassis.

Because NODO relies on timed moves, your feedforward constant ($kF$) must be tuned to overcome the friction of the foam tiles on your specific robot.

<div class="language-toggle" data-language-group="quickstart-main" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

## 1. Copy the Sample Auto
Create a new Java class in your folder called `NODOTestAuto` and paste the following code exactly as is. 

```java
package org.firstinspires.ftc.teamcode;

import com.nonodo.hardware.NODOChassis;
import com.nonodo.hardware.NODODrive;
import com.nonodo.hardware.NODODriveType;
import com.nonodo.hardware.NODOTankDrive;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Testing auton. Drives straight, then turns 90 degrees
 */
@Autonomous(name = "Sample Drive + Turn 90")
public class SampleDriveAndTurn90 extends LinearOpMode {

    private static double kF = 0.03;

    // ===== pick one of these =====
    private static final NODODriveType DRIVE_TYPE = NODODriveType.MECANUM;
    // private static final NODODriveType DRIVE_TYPE = NODODriveType.TANK;

    @Override
    public void runOpMode() {

        // ==== pick a drive type =====
        setMecanumNames("FL", "FR", "BL", "BR");

        // setTankNames("leftDrive", "rightDrive");

        NODODrive drive = new NODODrive(hardwareMap, DRIVE_TYPE, kF);

        configureMecanumDirections(drive,
                DcMotor.Direction.FORWARD, // FL direction
                DcMotor.Direction.REVERSE, // FR direction
                DcMotor.Direction.FORWARD,  // BL direction
                DcMotor.Direction.REVERSE); // BR direction

        // configureTankDirections(drive,
        //     DcMotor.Direction.FORWARD, // leftDrive direction
        //     DcMotor.Direction.REVERSE); // rightDrive direction

        drive.setControlHubOrientation(
                LogoFacingDirection.UP, UsbFacingDirection.FORWARD
        );

        drive.setTurnPD(0.04, 0.0025);

        telemetry.addData("drive", DRIVE_TYPE);
        telemetry.addLine("Ready.");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) {
            return;
        }

        // start of tele
        drive.driveFor(0.5, 800, this::opModeIsActive);
        drive.turnBy(-90, this::opModeIsActive); // +90 = CCW; use -90 for CW

        telemetry.addData("heading", drive.getHeading());
        telemetry.addLine("done");
        telemetry.update();
    }

    public void setMecanumNames(String fl, String fr, String bl, String br) {
        NODOChassis.setMotorNames(fl, fr, bl, br);
    }

    public void configureMecanumDirections(NODODrive drive,
                                           DcMotor.Direction flDir, DcMotor.Direction frDir,
                                           DcMotor.Direction blDir, DcMotor.Direction brDir) {
        drive.setMecanumMotorDirections(flDir, frDir, blDir, brDir);
    }

    public void setTankNames(String l, String r) {
        NODOTankDrive.setMotorNames(l, r);
    }

    public void configureTankDirections(NODODrive drive, DcMotor.Direction lDir, DcMotor.Direction rDir) {
        drive.setTankMotorDirections(lDir, rDir);
    }

}


```

## 2. Configure

1.  Change motor names to match hardware configuration. 

2. Reverse motors as needed
3. Update control hub orientation

    Directions are **robot relative**.

    Specific examples of orientation can be found [here](https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html).


## 3. Test!

Your robot should drive forwards, then turn 90 degrees CW.

If it doesn't turn exactly 90 degrees, or it shakes when turning, don't worry! Our next step, tuning, will help fix this.


</div>

<div data-language="Blocks" markdown="1">

## 1. Open FTC Blocks

In the Robot Controller web interface, go to **Blocks** and create a new **Autonomous** OpMode.

## 2. Build the Init sequence

In the **NODO Init** toolbox, drag these blocks into the **Init** section (before Start), in order:

```text
setMecanum("FL", "FR", "BL", "BR",
           "FORWARD", "REVERSE", "FORWARD", "REVERSE")

initializeMecanumDrive

setControlHubOrientation("UP", "FORWARD")

setTurnPD(0.04, 0.0025)
```

## 3. Configure

1. Change the motor names in `setMecanum` to match your hardware configuration.
2. Flip any `FORWARD`/`REVERSE` values to correct motor directions.
3. Update the logo and USB directions in `setControlHubOrientation`.

Specific orientation examples can be found [here](https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html).

## 4. Build the Run sequence

In the **NODO Run** toolbox, drag these blocks **after Start**, stacked directly below the Start hat — **not inside `repeat while opModeIsActive`**:

```text
driveFor(0.5, 800)

turnBy(-90)
```

## 5. Test!

Your robot should drive forwards, then turn 90 degrees CW.

If it doesn't turn exactly 90 degrees, or it shakes when turning, don't worry! Our next step, tuning, will help fix this.

</div>

</div>


## Visit [Tuning]({% link tuning.md %}) on how to tune.
