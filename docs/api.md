---
title: API Reference
layout: default
nav_order: 6
---

# NODO API Reference

A quick reference for NODO's drivetrain functions.

For a complete explanation of how to build an autonomous, see
[Building Your First Autonomous](building.md).

---

## Drive Commands

These are the core commands used to build an autonomous.

### `driveFor()`

Drive forward or backward for a set amount of time.

#### Java

```java
drive.driveFor(power, timeMs, this::opModeIsActive);
````

| Argument | Type              | Description                                            |
| -------- | ----------------- | ------------------------------------------------------ |
| `power`  | `double`          | Motor power from `-1.0` to `1.0`                       |
| `timeMs` | `long`            | Movement duration in milliseconds                      |
| `active` | `BooleanSupplier` | Stops the movement when the OpMode is no longer active |

#### Examples

```java
// Forward at 50% power for 800 ms
drive.driveFor(0.5, 800, this::opModeIsActive);

// Backward at 40% power for 500 ms
drive.driveFor(-0.4, 500, this::opModeIsActive);
```

The robot automatically attempts to maintain its heading while driving.

---

### `strafeFor()`

Strafe left or right for a set amount of time.

**Mecanum only.**

#### Java

```java
drive.strafeFor(power, timeMs, this::opModeIsActive);
```

| Argument | Type              | Description                                            |
| -------- | ----------------- | ------------------------------------------------------ |
| `power`  | `double`          | Strafe power from `-1.0` to `1.0`                      |
| `timeMs` | `long`            | Movement duration in milliseconds                      |
| `active` | `BooleanSupplier` | Stops the movement when the OpMode is no longer active |

#### Direction

* Positive → right
* Negative → left

#### Example

```java
drive.strafeFor(0.5, 700, this::opModeIsActive);
```

NODO uses IMU heading control while strafing.

---

### `turnBy()`

Turn the robot by a relative angle.

#### Java

```java
drive.turnBy(degrees, this::opModeIsActive);
```

| Argument  | Type              | Description                                        |
| --------- | ----------------- | -------------------------------------------------- |
| `degrees` | `double`          | Relative turn angle                                |
| `active`  | `BooleanSupplier` | Stops the turn when the OpMode is no longer active |

#### Direction

* Positive → typically counterclockwise
* Negative → typically clockwise

#### Examples

```java
drive.turnBy(90, this::opModeIsActive);

drive.turnBy(-90, this::opModeIsActive);
```

> `turnBy(90)` means **turn 90° from the current heading**, not "face 90° on the field."

Turns use the configured turn PD controller.

---

### `waitFor()`

Pause the autonomous while keeping the drivetrain stopped.

#### Java

```java
drive.waitFor(timeMs, this::opModeIsActive);
```

| Argument | Type              | Description                                       |
| -------- | ----------------- | ------------------------------------------------- |
| `timeMs` | `long`            | Wait duration in milliseconds                     |
| `active` | `BooleanSupplier` | Stops waiting when the OpMode is no longer active |

#### Example

```java
drive.waitFor(250, this::opModeIsActive);
```

---

# Drive Configuration

Configuration should be completed before `waitForStart()`.

---

## `setControlHubOrientation()`

Tell NODO how the Control Hub is mounted on the robot.

#### Java

```java
drive.setControlHubOrientation(
    LogoFacingDirection.UP,
    UsbFacingDirection.FORWARD
);
```

| Argument     | Description                  |
| ------------ | ---------------------------- |
| `logoFacing` | Direction the REV logo faces |
| `usbFacing`  | Direction the USB ports face |

Valid directions:

```text
UP
DOWN
FORWARD
BACKWARD
LEFT
RIGHT
```

The orientation must match the **physical mounting of the Control Hub**.

Incorrect orientation can cause incorrect heading readings or turns in the wrong direction.

---

## `setExpansionHubOrientation()`

Configure the orientation of an optional Expansion Hub IMU.

#### Java

```java
drive.setExpansionHubOrientation(
    LogoFacingDirection.UP,
    UsbFacingDirection.FORWARD
);
```

Only configure this when using an Expansion Hub IMU.

---

## `setMecanumMotorDirections()`

Set the direction of each mecanum motor.

#### Java

```java
drive.setMecanumMotorDirections(
    DcMotor.Direction.FORWARD, // front left
    DcMotor.Direction.REVERSE, // front right
    DcMotor.Direction.FORWARD, // back left
    DcMotor.Direction.REVERSE  // back right
);
```

Order:

```text
Front Left
Front Right
Back Left
Back Right
```

Positive forward power should make the robot physically move forward.

---

## `setTankMotorDirections()`

Set the direction of the two tank drivetrain motors.

```java
drive.setTankMotorDirections(
    DcMotor.Direction.FORWARD,
    DcMotor.Direction.REVERSE
);
```

Order:

```text
Left
Right
```

---

## `setMotorNames()`

Set custom hardware-map motor names.

Use this **before constructing the drive**.

### Mecanum

```java
NODOChassis.setMotorNames(
    "frontLeft",
    "frontRight",
    "backLeft",
    "backRight"
);
```

### Default names

```text
frontLeft
frontRight
backLeft
backRight
```

### Tank

```text
leftDrive
rightDrive
```

If your Robot Controller configuration uses different names, replace the defaults with your names.

---

# Turn Control

NODO uses a PD controller for IMU-based turns.

---

## `setTurnPD()`

Configure the turn controller.

#### Java

```java
drive.setTurnPD(kP, kD);
```

or:

```java
drive.setTurnPD(kP, kD, maxPower);
```

| Argument   | Description              |
| ---------- | ------------------------ |
| `kP`       | Proportional gain        |
| `kD`       | Derivative gain          |
| `maxPower` | Maximum turn motor power |

#### Example

```java
drive.setTurnPD(0.04, 0.0025, 0.4);
```

### Starting values

| Setting    | Starting value |
| ---------- | -------------: |
| `kP`       |        `0.035` |
| `kD`       |        `0.002` |
| `maxPower` |         `0.35` |

These are starting points, not universal values.

---

## `setTurnToleranceDegrees()`

Set how close the robot must be to the target heading.

```java
drive.setTurnToleranceDegrees(3.0);
```

Example:

```text
Target:     90°
Tolerance:  3°

Accepted range:
87° → 93°
```

---

## `setTurnSettleMs()`

Set how long the robot must remain within the turn tolerance before the turn is considered complete.

```java
drive.setTurnSettleMs(150);
```

A longer settle time can make the turn more stable but increases autonomous time.

---

## `getTurnPD()`

Access the turn controller directly.

```java
drive.getTurnPD()
    .setPD(0.04, 0.0025)
    .setMaxPower(0.4)
    .setToleranceDegrees(3.0);
```

Use the individual setter functions if you prefer simpler configuration.

---

# Heading

## `getHeading()`

Return the filtered heading.

```java
double heading = drive.getHeading();
```

Useful for telemetry and display.

---

## `getRawHeading()`

Return the raw IMU heading.

```java
double heading = drive.getRawHeading();
```

Useful for debugging IMU behavior and turns.

---

# TeleOp / Manual Drive

NODO can also control the drivetrain during TeleOp.

---

## `setMecanumPowers()`

Directly set the four mecanum motor powers.

```java
chassis.setMecanumPowers(
    frontLeft,
    frontRight,
    backLeft,
    backRight
);
```

Example:

```java
chassis.setMecanumPowers(fl, fr, bl, br);
```

Power values are generally between `-1.0` and `1.0`.

---

## `setPowers()`

Set left and right tank drivetrain power.

```java
tank.setPowers(left, right);
```

---

## `resetYaw()`

Reset the heading reference.

```java
chassis.resetYaw();
```

This can be useful during TeleOp when you want to redefine the current robot heading as zero.

---

# NODODrive

`NODODrive` is the general drivetrain wrapper.

It allows a program to select mecanum or tank drive through the same class.

## Constructor

```java
NODODrive drive = new NODODrive(
    hardwareMap,
    NODODriveType.MECANUM,
    0.03
);
```

| Argument        | Description                   |
| --------------- | ----------------------------- |
| `hardwareMap`   | FTC hardware map              |
| `NODODriveType` | `MECANUM` or `TANK`           |
| `kF`            | Static drivetrain feedforward |

### Drive types

```java
NODODriveType.MECANUM
NODODriveType.TANK
```

---

# kF

`kF` is the small static feedforward used to help the drivetrain overcome friction.

Typical starting value:

```java
0.03
```

Example:

```java
NODODrive drive = new NODODrive(
    hardwareMap,
    NODODriveType.MECANUM,
    0.03
);
```

### If kF is too low

The robot may:

* Struggle to start moving
* Feel sluggish at low power

### If kF is too high

The robot may:

* Jump or twitch when starting
* Feel aggressive near zero power

> kF is for the drivetrain. It should not be applied to arms, slides, or other mechanisms.

---

# NODORoutine

`NODORoutine` is **optional**.

You do not need it to build an autonomous.

Use direct drivetrain helpers if you want the simplest approach.

## Create a routine

```java
NODORoutine routine = new NODORoutine(chassis)
    .drive(0.5, 800)
    .waitMs(200)
    .turnTo(90)
    .drive(0.4, 500);
```

---

## `drive()`

Add a timed drive step.

```java
routine.drive(power, timeMs);
```

Example:

```java
routine.drive(0.5, 800);
```

---

## `waitMs()`

Add a wait step.

```java
routine.waitMs(timeMs);
```

Example:

```java
routine.waitMs(200);
```

---

## `turnTo()`

Add a relative turn step.

```java
routine.turnTo(degrees);
```

Example:

```java
routine.turnTo(90);
```

---

## `start()`

Start the routine.

```java
routine.start();
```

Call this when the OpMode starts.

---

## `loop()`

Advance the routine by one scheduler cycle.

```java
routine.loop();
```

Call this from the OpMode's `loop()` method.

---

## `isFinished()`

Check whether every routine step has completed.

```java
if (routine.isFinished()) {
    // autonomous is complete
}
```

---

## `cancelAll()`

Stop and cancel the routine.

```java
routine.cancelAll();
```

Usually called from `stop()`.

---

# FTC Blocks Quick Reference

The Blocks API exposes the most common autonomous functions.

| Blocks block                 | Purpose                             |
| ---------------------------- | ----------------------------------- |
| `initializeMecanumDrive`     | Initialize mecanum drive            |
| `initializeTankDrive`        | Initialize tank drive               |
| `initializeDrive`            | Initialize selected drive type      |
| `setControlHubOrientation`   | Configure Control Hub orientation   |
| `setExpansionHubOrientation` | Configure Expansion Hub orientation |
| `setTurnPD`                  | Configure turn PD                   |
| `driveStraight`              | Timed forward/backward movement     |
| `strafe`                     | Timed mecanum strafe                |
| `turnToHeading`              | Relative IMU turn                   |
| `waitSeconds`                | Pause                               |

### Example

```text
initializeMecanumDrive

setControlHubOrientation("UP", "FORWARD")

setTurnPD(0.04, 0.0025, 0.4)

        ↓ PLAY

driveStraight(0.8, 0.5)

waitSeconds(0.15)

turnToHeading(90, 0.8)

driveStraight(0.5, 0.4)
```

> Blocks does not require `NODORoutine`. Stack movement blocks in the order you want them to execute.

---

# Common Patterns

## Simple autonomous

```java
drive.driveFor(0.5, 800, this::opModeIsActive);
drive.turnBy(90, this::opModeIsActive);
drive.driveFor(0.4, 500, this::opModeIsActive);
```

## Drive → mechanism → drive

```java
drive.driveFor(0.5, 800, this::opModeIsActive);

// Your mechanism code here
arm.setPower(1.0);

drive.waitFor(300, this::opModeIsActive);

drive.driveFor(-0.4, 500, this::opModeIsActive);
```

## Drive → strafe → turn

```java
drive.driveFor(0.5, 700, this::opModeIsActive);
drive.strafeFor(0.4, 500, this::opModeIsActive);
drive.turnBy(-90, this::opModeIsActive);
```

---

# Quick Setup Reference

For a new Java autonomous:

```java
// 1. Configure motor names if necessary

NODOChassis.setMotorNames(
    "frontLeft",
    "frontRight",
    "backLeft",
    "backRight"
);

// 2. Create drive

NODODrive drive = new NODODrive(
    hardwareMap,
    NODODriveType.MECANUM,
    0.03
);

// 3. Configure IMU

drive.setControlHubOrientation(
    LogoFacingDirection.UP,
    UsbFacingDirection.FORWARD
);

// 4. Configure motor directions

drive.setMecanumMotorDirections(
    DcMotor.Direction.FORWARD,
    DcMotor.Direction.REVERSE,
    DcMotor.Direction.FORWARD,
    DcMotor.Direction.REVERSE
);

// 5. Optional: tune turns

drive.setTurnPD(0.035, 0.002, 0.35);

// 6. Start autonomous

waitForStart();

if (isStopRequested()) return;

// 7. Build your sequence

drive.driveFor(0.5, 800, this::opModeIsActive);
drive.turnBy(90, this::opModeIsActive);
drive.driveFor(0.4, 500, this::opModeIsActive);
```

---

# At a Glance

```text
DRIVE
driveFor()
strafeFor()

TURN
turnBy()

TIMING
waitFor()

CONFIGURATION
setControlHubOrientation()
setExpansionHubOrientation()
setMecanumMotorDirections()
setTankMotorDirections()
setMotorNames()

TURN TUNING
setTurnPD()
setTurnToleranceDegrees()
setTurnSettleMs()
getTurnPD()

HEADING
getHeading()
getRawHeading()
resetYaw()

TELEOP
setMecanumPowers()
setPowers()

OPTIONAL ROUTINES
NODORoutine
.drive()
.waitMs()
.turnTo()
.start()
.loop()
.isFinished()
.cancelAll()
```

---

## Need an Explanation?

This page is intended as a **quick reference**.

For step-by-step instructions:

* [Build Your First Autonomous →](building.md)
* [Chassis Configuration →](chassis.md)
* [Tuning →](tuning.md)
* [FTC Blocks →](blocks.md)
* [Routines →](routines.md)

```
```
