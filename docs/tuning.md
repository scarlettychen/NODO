---
title: Tuning Guide
layout: default
nav_order: 4
---

# Tuning Guide

Because NODO relies on timed moves rather than dead-wheel odometry, your robot's consistency depends heavily on tuning these values: **kF (Feedforward)** and **Turn PD**.

Choose your programming environment for instructions.

---

## 1. Tuning kF (Static Feedforward)

**Why this:** Foam tiles have a lot of static friction. `kF` instantly gives the amount of power to counter that friction.

- **Too Low:** The robot buzzes but struggles to move at low speeds.
- **Too High:** The robot jerks violently at the start of every move.
- **Starting Value:** `0.03`

### How to Apply kF

<div class="language-toggle" data-language-group="tuning-kf" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

In Java, your `kF` value is the **third argument** passed into your drive constructor. You only need to set this once in your `init()` or `runOpMode()` method.

However, on the sample tele which you will use for test, we have put kF at the top for easy access.

Find this:

```java
private static double kF = 0.03;
```

</div>

<div data-language="Blocks" markdown="1">

You can use the dedicated setter to set kF.

1. Look in the **NODO Init** Blocks menu.
2. Drag out the **`setFeedforward(kF)`** block.
3. Snap a math number block to it and set it to `0.03`.
4. Place this directly below your `initializeMecanumDrive` block.

</div>

</div>

---

## 2. Tuning Turn PD (Proportional-Derivative)

**What this is:** A PD controller calculates how much power to send to the motors to reach a target angle without overshooting.

| Value          | Meaning                                                                                                                                                                     | Default |
| -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------: |
| **`kP`**       | **Proportional** The further you are from the target, the more power it gives. Raise if turns are sluggish; lower if it aggressively overshoots. | `0.035` |
| **`kD`**       | **Derivative** Resists fast turning speeds to reduce overshoot and oscillation. Raise it slightly if the robot whips past the target; lower if robot agressively oscillates.                        | `0.002` |
| **`maxPower`**       | **Max Power** Limits maximum power given to motors while turning.                      | `0.8` |


### The Tuning Process

1. In Quickstart Auton, remove the drive command, leaving `turnBy(-90)`. 
    
    Robot should turn 90 degrees counter-clockwise.
2. Play Tele.
3. Watch  robot carefully.
4. Adjust `kP` and `kD` until it reaches 90 degrees quickly and stops without wiggling.
5. To retest, you'll need to restart the OpMode.

### How to Apply Turn PD

<div class="language-toggle" data-language-group="tuning-pd" markdown="1">

<div data-language="Java/OnBot Java" markdown="1">

**This is already included in the Quickstart Auton**

Use `setTurnPD()` on your drive object before starting your autonomous sequence.

```java
// drive.setTurnPD(kP, kD, maxPower);
drive.setTurnPD(0.04, 0.0025, 0.4);

// You can also change the tolerance
// (how close is good enough for finishing)
drive.setTurnToleranceDegrees(3.0);
```

</div>

<div data-language="Blocks" markdown="1">

Use the dedicated Turn PD block to override the default values.

1. Look in the **NODO Init** Blocks menu.
2. Drag out the **`setTurnPD(kP, kD, maxPower)`** block.
3. Snap three math number blocks to it.
4. Set them to `0.04`, `0.0025`, and `0.4`, respectively.
5. Place this in your initialization sequence, before running any `turnBy` blocks.

</div>

</div>

---

## Troubleshooting

If your tuning doesn't seem to be working, check these common issues:

* **Robot spins the completely wrong way:** Your Hub Orientation or Motor Directions are configured incorrectly.
* **Robot never finishes the turn:** Your Turn Tolerance may be too strict. The robot may be trying to get within 1 degree but cannot consistently do so. Raise the tolerance (for example, to `3.5` degrees).

---

## Next Steps

Once your robot is tuned, you are ready to write complex autonomous paths.

* [**Building Autons →**]({% link building.md %})


