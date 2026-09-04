# NODO

NODO is a lightweight FTC autonomous framework for teams without odometry pods. It combines battery voltage compensation, IMU gyro correction, and time-based movements for reliable autons on mecanum or tank.

**Do not fork this repo.** Add it as a Gradle dependency to your existing FTC `TeamCode` module (you already have the FTC SDK / Robot Controller project).

## How teams use it

1. Keep your normal [FtcRobotController](https://github.com/FIRST-Tech-Challenge/FtcRobotController) project.
2. Add NODO in `TeamCode/build.gradle` (see below).
3. Sync Gradle, then write OpModes in `TeamCode` that use `NODOChassis` (required) and optionally `NODORoutine` / commands.
4. Optionally copy examples from this repo’s [`samples/`](samples/) folder into your `org.firstinspires.ftc.teamcode` package and edit hardware names.
5. For Blocks / OnBot, see the [docs](https://scarlettychen.github.io/NODO/).

### Gradle

In `TeamCode/build.gradle`:

```gradle
repositories {
    mavenCentral()
    google()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation project(':FtcRobotController')

    implementation 'com.github.scarlettychen:NODO:v1.0.0-beta.1'
}
```

Then sync. Classes under `com.nonodo` resolve against the FTC SDK already on your classpath.

JitPack coordinates: **GitHub** `scarlettychen`, **repo** `NODO`, **tag** `v1.0.0-beta.1`.

When you sync / build, Gradle:

1. Looks in the repositories you listed (Maven Central, JitPack, etc.).
2. Downloads the NODO AAR for that tag.
3. Puts it on TeamCode's compile + runtime classpath so you can `import com.nonodo...` and the classes are packaged into the Robot Controller APK you deploy.

NODO itself uses `compileOnly` for FTC `RobotCore` / `Hardware`, so those are **not** inside the AAR — your existing FTC project already provides them.

**OnBot / Blocks:** download [`nodo-1.0.0-beta.1.jar`](https://github.com/scarlettychen/NODO/releases/latest) from GitHub Releases and upload via OnBot Java. See the [docs](https://scarlettychen.github.io/NODO/installation/).

### Chassis only (no commands)

Commands and `NODORoutine` are optional. TeleOp and LinearOpMode autos can use the chassis directly:

```java
NODOChassis.setMotorNames("frontLeft", "frontRight", "backLeft", "backRight");
NODOChassis chassis = new NODOChassis(hardwareMap, 0.03);
// setControlHubOrientation / setMotorDirections as needed

waitForStart();

chassis.driveFor(0.5, 800, this::opModeIsActive);
chassis.strafeFor(0.4, 600, this::opModeIsActive);
chassis.turnBy(-90, this::opModeIsActive);
```

TeleOp: compute mecanum powers yourself and call `chassis.setMecanumPowers(...)`.

### Optional: routines / commands

If you prefer an iterative OpMode with a queued sequence:

```java
routine = new NODORoutine(chassis)
        .drive(0.5, 800)
        .waitMs(300)
        .strafe(0.4, 600)
        .turnTo(-90)
        .drive(0.5, 400);

// start(): routine.start();
// loop():  routine.loop();
// stop():  routine.cancelAll();
```

## Docs

Full auton walkthrough: see [`docs/building.md`](docs/building.md) (published at [https://scarlettychen.github.io/NODO/](https://scarlettychen.github.io/NODO/)).

## Privacy

On first init on a Control Hub, NODO may send one anonymous ping (`library` + `version` only). Opt out before constructing chassis:

```java
com.nonodo.UsageTracker.enabled = false;
```
