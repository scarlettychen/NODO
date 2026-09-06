---
title: Home
layout: default
nav_order: 1
has_children: false
---

# Welcome to NODO!
### NODO is a lightweight FTC drivetrain library designed for robots that don't use dead-wheel odometry.

NODO uses hardware already on your robot (drivetrain motors, battery voltage, and Control Hub IMU) to make simple, repeatable autonomous routines without additional hardware

## Dive straight into installation?
<br>
<p><a href="{{ '/installation/' | relative_url }}" class="btn btn-primary">Install!</a></p>


## Why NODO?

In FIRST Tech Challenge,  high consistent autonomous routines often rely on dead-wheel odometry.

Dead wheels provide accurate field localization, but they also add:
* Additional hardware
* More configuration & tuning
* An added cost some teams may not be able to afford

Traditional time-based autonomous is simpler, but introduces another crucial problem.

A command such as:

`driveFor(0.5, 800);`

doesn't necessarily travel the same distance every run. Battery voltage, friction, and more can change the distance.

### NODO sits between these two approaches.
Keeping simplicity of timed autonomous while using software controls to make drivetrain behavior more consistent.

## Some Core Features
*   **Voltage Compensation:** Automatically scales motor power based on battery drops using a first-order low pass filter.
*   **IMU Heading Hold:** Uses a PD Controller & the Control Hub's internal IMU to keep the robot driving/strafing straight
*   **Simple Routine Builder:** Chains commands together easily, all with little learning curve.
*   **FTC Blocks & OnBot Java Support:** Support for OnBot Java and Blocks programmers!

## NODO does:
* Drive forward/backward for a specified time
* Strafe for a specified time on mecanum drivetrains
* Turn by a relative angle using IMU
* Apply feedforward to overcome drivetrain friction
* Build sequences of autonomous movements
* Self correct for small heading changes while driving straight/strafing
* Support both Java and FTC Blocks

## NODO is not:
NODO is **not** a replacement for full field localization.

It does not:
* Track field position
* Use dead-wheel odometry
* Follow trajectories

**NODO is designed around short, repeatable movements, and IMU based heading control.**

## Recommended Learning Path
1. [Install NODO]({{ '/installation/' | relative_url }}) 

    Add NODO to your FTC project.
2. [Quickstart]({{ '/quickstart/' | relative_url }})

    Get your robot moving!
3. [Configure your chassis]({{ '/quickstart/' | relative_url }})

    Set up motors, directions, and IMU orientation.
4. [Tune]({{ '/tuning/' | relative_url }})

   Tune feedforward and turn controls for your robot

5. [Build your Autonomous]({{ '/building/' | relative_url }})

   Learn movement commands and build complete sequences.

### Do you already know NODO?
[Browse API Reference →]({{ '/api/' | relative_url }})







---
#### NODO
*By Scarlett C. from Team 8393, the Giant Diencephalic BrainSTEM Robotics Team*

