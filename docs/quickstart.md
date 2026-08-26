---
layout: default
title: Quickstart
nav_order: 3
---

# Quickstart

Before you write your own autonomous paths, copy our sample OpMode to verify your hardware is working and to tune your chassis.

Because NODO relies on timed moves, your feedforward constant ($kF$) must be tuned to overcome the friction of the foam tiles on your specific robot.

<div class="language-toggle" data-language-group="some-unique-id" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

## 1. Copy the Sample Auto
Create a new Java class in your folder called `NODOTestAuto` and paste the following code exactly as is. 

```java
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
        setMecanum("FL", "FR", "BL", "BR", 
            DcMotor.Direction.FORWARD, // FL direction 
            DcMotor.Direction.REVERSE, // FR direction 
            DcMotor.Direction.FORWARD,  // BL direction
            DcMotor.Direction.REVERSE); // BR direction

        // setTank("leftDrive", "rightDrive", 
        //     DcMotor.Direction.FORWARD, // leftDrive direction
        //     DcMotor.Direction.REVERSE); // rightDrive direction

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

        // start of tele
        drive.driveFor(0.5, 800, this::opModeIsActive);
        drive.turnBy(-90, this::opModeIsActive); // +90 = CCW; use -90 for CW

        telemetry.addData("heading", drive.getHeading());
        telemetry.addLine("done");
        telemetry.update();
    }

   public void setMecanum(String fl, String fr, String bl, String br
                            DcMotor.Direction flDir, DcMotor.Direction frDir, DcMotor.Direction blDir, DcMotor.Direction brDir
    ) {
        NODOChassis.setMotorNames(fl, fr, bl, br);
        NODOChassis.setMotorDirections(flDir, frDir, blDir, brDir);
    }

    public void setTank(String l, String r, DcMotor.Direction lDir, DcMotor.Direction rDir
    ) {
        NODOChassis.setMotorNames(l, r);
        NODOChassis.setMotorDirections(lDir, rDir);
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

## 1. Download the NODO `.blk` file
In the Release section of the NODO github page, download the **NODO TeleOp `.blk` file**. 

## 2. Open Blocks & Upload the `.blk`

Use **Upload/Import** in Blocks, and select the `.blk` file you just downloaded.

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

</div>


## Visit [Tuning]({% link tuning.md %}) on how to tune.
