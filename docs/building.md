---
title: Building Your First Autonomous
layout: default
nav_order: 5
---

# Building Your First Autonomous

This guide walks you through building a complete NODO autonomous from scratch.

You will learn:

* How NODO autonomous movement works
* The basic movement commands
* How turns and heading work
* How to combine movements into a sequence
* How to add your own mechanisms
* When to use `NODORoutine`
* How to build the same autonomous with FTC Blocks

> **You do not need to use NODORoutine to build an autonomous.**
>
> For most teams, the simplest approach is to call NODO's drivetrain helpers directly from a `LinearOpMode`.

---

## 1. How NODO Autonomous Works

NODO is designed for **timed autonomous movement with IMU feedback**.

Instead of telling the robot:

> "Go to `(x, y)`."

you tell it:

> "Drive at this power for this long."

NODO then uses the Control Hub IMU to help keep the robot pointed in the correct direction while it moves.

For example:

```java
drive.driveFor(0.5, 800, this::opModeIsActive);
```

means:

> Drive forward at 50% power for 800 milliseconds while maintaining the current heading.

NODO's autonomous system is built around four basic types of movement:

| Command       | Purpose                        |
| ------------- | ------------------------------ |
| `driveFor()`  | Drive forward or backward      |
| `strafeFor()` | Strafe left or right           |
| `turnBy()`    | Turn by a relative angle       |
| `waitFor()`   | Pause before the next movement |

These commands are enough to build a complete autonomous.

---

# 2. The Simplest Way to Build an Auto

NODO does **not** require a command framework.

You can simply write your autonomous from top to bottom:

```java
drive.driveFor(0.5, 800, this::opModeIsActive);

drive.turnBy(90, this::opModeIsActive);

drive.driveFor(0.4, 500, this::opModeIsActive);
```

The robot will:

1. Drive forward for 800 ms
2. Turn 90°
3. Drive forward for 500 ms

Each movement finishes before the next one begins.

This is the recommended approach for teams who are new to NODO.

---

# 3. Movement

The most basic NODO command is `driveFor()`.

<div class="language-toggle" data-language-group="building-drive" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

```java
drive.driveFor(0.5, 800, this::opModeIsActive);
```

</div>

<div data-language="Blocks" markdown="1">

ftc blocks image needed

</div>

</div>

The command has two important values:

* **Power** — how fast the robot drives
* **Time** — how long the robot drives

Positive power drives forward; Negative power drives backward.

For example:

```java
drive.driveFor(-0.4, 500, this::opModeIsActive);
```

drives backward at 40% power for 500 ms.

### Important

NODO is **time-based**, not distance-based.

An 800 ms movement does not mean:

> "Move exactly 24 inches."

It means:

> "Drive for 800 ms."

The distance traveled depends on the robot, power given, and tuning.

You should therefore tune your movement times on the actual field.

---

# 4. Strafing

Strafing is available on mecanum drivetrains.

<div class="language-toggle" data-language-group="building-strafe" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

```java
drive.strafeFor(0.5, 700, this::opModeIsActive);
```

Positive power:

> Strafe right

Negative power:

> Strafe left

</div>

<div data-language="Blocks" markdown="1">

need blocks image

</div>

</div>

Strafing is **mecanum-only**.

Tank drivetrains do not have a `strafeFor()` command.

---

# 5. Heading Hold

One of the main advantages of NODO over basic timed autonomous is that the robot does not simply drive with open-loop motor power.

At the beginning of a drive or strafe, NODO records the robot's current IMU heading.

It then uses the IMU to correct small heading errors while the robot moves.

For example:

```java
drive.driveFor(0.5, 1000, this::opModeIsActive);
```

If the robot begins to rotate slightly while driving, NODO applies a small correction to try to bring the robot back toward its original heading.

### You do not need to manually control heading during a normal drive.

NODO handles this automatically.

---

# 6. Turning

Use `turnBy()` when you want the robot to rotate by a specific amount.

<div class="language-toggle" data-language-group="building-turn" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

```java
drive.turnBy(90, this::opModeIsActive);
```

</div>

<div data-language="Blocks" markdown="1">

need blocks image

</div>

</div>

This means:

> Turn **90° relative to the robot's current heading**.

A negative value turns the opposite direction.

### Turns are relative

Suppose the robot currently faces 30°.

```java
drive.turnBy(90, this::opModeIsActive);
```

does **not** mean:

> Face 90°.

It means:

> Turn another 90° from the current heading.

So the robot will end up approximately at 120°.

This makes it easy to build sequences without needing to track the robot's absolute field heading manually.

---

# 7. Waiting

Use `waitFor()` when you want the robot to stop before continuing.

<div class="language-toggle" data-language-group="building-wait" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

```java
drive.waitFor(250, this::opModeIsActive);
```

</div>

<div data-language="Blocks" markdown="1">

need blocks image

</div>

</div>

This waits for 250 milliseconds with the drivetrain stopped.

Waiting can be useful when:

* A mechanism needs time to settle
* You want to pause between movements
* Your robot needs a short delay before scoring
* You want to separate aggressive movements

**Pausing between drive movements will make the robot drift less!!!**

---

# 8. Combining Commands

The real power of NODO comes from combining simple movements.

For example:

```java
drive.driveFor(0.5, 800, this::opModeIsActive);

drive.waitFor(150, this::opModeIsActive);

drive.turnBy(90, this::opModeIsActive);

drive.waitFor(100, this::opModeIsActive);

drive.strafeFor(0.4, 500, this::opModeIsActive);

drive.driveFor(-0.4, 300, this::opModeIsActive);
```

This creates a complete sequence:

```text
Drive
 ↓
Wait
 ↓
Turn
 ↓
Wait
 ↓
Strafe
 ↓
Back up
```

You can continue stacking commands for as many steps as your autonomous requires.

---

# 9. Adding Your Mechanisms

NODO only controls the **drivetrain**.

Your arms, slides, intakes, claws, servos, and other mechanisms remain normal FTC code.

For example:

```java
drive.driveFor(0.5, 800, this::opModeIsActive);

arm.setPower(1.0);

drive.waitFor(300, this::opModeIsActive);

intake.setPower(1.0);

drive.driveFor(0.3, 400, this::opModeIsActive);
```

You can put your normal FTC mechanism code between NODO commands.

NODO does not require you to rewrite your existing mechanism code.

---

# 10. A Complete Autonomous

Once you understand the basic commands, you can combine them into a real sequence.

```java
@Override
public void runOpMode() {

    NODODrive drive = new NODODrive(
            hardwareMap,
            NODODriveType.MECANUM,
            0.03
    );

    drive.setControlHubOrientation(
            LogoFacingDirection.UP,
            UsbFacingDirection.FORWARD
    );

    drive.setMecanumMotorDirections(
            DcMotor.Direction.FORWARD,
            DcMotor.Direction.REVERSE,
            DcMotor.Direction.FORWARD,
            DcMotor.Direction.REVERSE
    );

    telemetry.addLine("Ready");
    telemetry.update();

    waitForStart();

    if (isStopRequested()) return;

    // Leave starting area
    drive.driveFor(0.5, 800, this::opModeIsActive);

    // Turn toward scoring location
    drive.turnBy(-90, this::opModeIsActive);

    // Approach scoring location
    drive.driveFor(0.4, 600, this::opModeIsActive);

    // Your mechanism code
    // arm...
    // intake...
    // servo...

    // Back away
    drive.driveFor(-0.4, 400, this::opModeIsActive);
}
```

This is a complete NODO autonomous.

You do **not** need `NODORoutine`.

---

# 11. Do I Need `NODORoutine`?

**No.**

`NODORoutine` is completely optional.

NODO provides multiple ways to build the same autonomous:

### Option 1 — Direct drivetrain helpers

```java
drive.driveFor(...);
drive.turnBy(...);
drive.waitFor(...);
```

This is the **simplest option** and is recommended for most teams.

### Option 2 — `NODORoutine`

You can instead create a sequence:

```java
routine = new NODORoutine(chassis)
        .drive(0.5, 800)
        .waitMs(200)
        .turnTo(90)
        .drive(0.4, 500);
```

The routine then manages which step is currently running.

### Option 3 — FTC Blocks

Blocks uses the same basic idea as direct drivetrain helpers:

```text
driveStraight
    ↓
waitSeconds
    ↓
turnToHeading
    ↓
driveStraight
```

All three approaches use the same underlying NODO motion system.

---

# 12. When Should I Use a Routine?

For a short autonomous, direct helpers are usually easier:

```java
drive.driveFor(...);
drive.turnBy(...);
drive.driveFor(...);
```

As an autonomous becomes longer, a routine can make the sequence easier to read.

### Direct helpers

Best for:

* Short autos
* Beginners
* Simple scoring sequences
* Teams who want code that reads top-to-bottom

### `NODORoutine`

Best for:

* Longer sequences
* Teams that prefer a step-based structure
* Iterative `OpMode` programs
* Teams that want the autonomous sequence separated from the initialization code

There is **no performance advantage simply from using a routine**.

It is rather an organizational tool.

**There is no blocks version of NODORoutine**

---
# 13. Building Your Autonomous: Recommended Workflow

Don't try to write your entire competition autonomous at once.

Build it one movement at a time.

### Step 1 — Test driving

Start with:

```java
drive.driveFor(0.4, 500, this::opModeIsActive);
```

Make sure:

* Forward is actually forward
* The robot drives straight
* Motor directions are correct

### Step 2 — Test a turn

Add:

```java
drive.turnBy(90, this::opModeIsActive);
```

Make sure the robot turns the correct direction.

### Step 3 — Add another movement

Add the next drive, strafe, or turn.

### Step 4 — Add mechanisms

Once the drivetrain sequence works, add your arm, intake, claw, or other mechanisms.

### Step 5 — Tune timings

Run the autonomous repeatedly on your competition tiles.

Adjust the time for each movement until the robot consistently reaches the desired location.

### Step 6 — Build the full sequence

Only after individual movements work should you combine them into the final autonomous.

---

# 14. Important: Time-Based Movement Has Limits

NODO makes timed autonomous more consistent, but it does **not** turn timed movement into position-based localization.

For example:

```java
drive.driveFor(0.5, 800, this::opModeIsActive);
```

will not travel exactly the same distance under every possible condition.

Battery voltage, wheel slip, friction, acceleration, and other mechanical differences can affect the final position.

Higher drive powers can also make timed movements more sensitive to these differences.

This is why NODO is best suited to **short, deliberate movements that can be tuned on the competition field**.

NODO's voltage compensation and IMU control reduce some sources of variation, but teams should still tune their movement times on their actual robot and field surface.

> **NODO makes time-based autonomous more consistent. It does not eliminate the fundamental limitations of time-based movement.**

---
**[API Reference →]({% link api.md %})**
Explore every NODO function in detail.
