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
        // ==== pick one =====
        setMecanum("FL", "FR", "BL", "BR",
                DcMotor.Direction.FORWARD,
                DcMotor.Direction.REVERSE,
                DcMotor.Direction.FORWARD,
                DcMotor.Direction.REVERSE);

        // setTank("leftDrive", "rightDrive",
        //     DcMotor.Direction.FORWARD,
        //     DcMotor.Direction.REVERSE);

        NODODrive drive = new NODODrive(hardwareMap, DRIVE_TYPE, kF);

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

        // LinearOpMode: call helpers in order — no start()/loop() needed.
        drive.driveFor(0.5, 800, this::opModeIsActive);
        drive.turnBy(-90, this::opModeIsActive); // +90 = CCW; use -90 for CW

        telemetry.addData("heading", drive.getHeading());
        telemetry.addLine("done");
        telemetry.update();
    }

    public void setMecanum(String fl, String fr, String bl, String br,
                           DcMotor.Direction flDir, DcMotor.Direction frDir,
                           DcMotor.Direction blDir, DcMotor.Direction brDir) {
        NODOChassis.setMotorNames(fl, fr, bl, br);
        NODOChassis.setMotorDirections(flDir, frDir, blDir, brDir);
    }

    public void setTank(String l, String r, DcMotor.Direction lDir, DcMotor.Direction rDir) {
        NODOTankDrive.setMotorNames(l, r);
        NODOTankDrive.setMotorDirections(lDir, rDir);
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

In the Robot Controller web interface, go to **Blocks** and import **`TestOpmode.blk`** (downloaded from Releases in Installation).

## 2. Configure

1. Change the motor names in `setMecanum` to match your hardware configuration.
2. Flip any `FORWARD`/`REVERSE` values to correct motor directions.
3. Update the logo and USB directions in `setControlHubOrientation`.

Specific orientation examples can be found [here](https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html).

## 3. Build the Run sequence

In the **NODO Run** toolbox, drag these blocks **after Start**, stacked directly below the Start hat — **not inside `repeat while opModeIsActive`**:

```text
driveFor(0.5, 800)

turnBy(-90)
```

## 4. Test!

Your robot should drive forwards, then turn 90 degrees CW.

If it doesn't turn exactly 90 degrees, or it shakes when turning, don't worry! Our next step, tuning, will help fix this.

</div>

</div>


<a href="{{ '/tuning/' | relative_url }}" class="btn btn-primary">Go to Tuning</a>
