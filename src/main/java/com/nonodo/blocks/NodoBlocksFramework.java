package com.nonodo.blocks;

import com.nonodo.command.NoOdoCommand;
import com.nonodo.command.drive.DriveStraightCommand;
import com.nonodo.hardware.NoOdoChassis;
import com.nonodo.hardware.SmartDriveMotor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.ExportClassToBlocks;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Java-for-Blocks companion for rookie and budget teams that do not use dead-wheel odometry.
 * This is not a runnable OpMode. Drop {@code initializeDrive} into a Blocks init stack, then
 * call {@code driveStraight} / {@code turnToHeading} from the run stack.
 *
 * <p>Mecanum reuses {@link NoOdoChassis} and {@link DriveStraightCommand}. Tank uses the same
 * {@link SmartDriveMotor} voltage compensation and gyro math on {@code leftDrive}/{@code rightDrive}.
 */
@ExportClassToBlocks
public class NodoBlocksFramework extends BlocksOpModeCompanion {

    public static final int DRIVE_TYPE_MECANUM = 0;
    public static final int DRIVE_TYPE_TANK = 1;

    private static final double DEFAULT_KF = 0.03;
    private static final double HEADING_P_GAIN = 0.02;
    private static final double TURN_P_GAIN = 0.02;
    private static final double TURN_I_GAIN = 0.002;
    private static final double TURN_D_GAIN = 0.004;
    private static final double HEADING_TOLERANCE_DEG = 2.0;
    private static final double TURN_SETTLE_MS = 150.0;
    private static final double TURN_TIMEOUT_SEC = 5.0;
    private static final double DEFAULT_MAX_TURN_POWER = 0.8;

    private static int driveType = DRIVE_TYPE_MECANUM;
    private static boolean driveInitialized;

    private static NoOdoChassis chassis;
    private static SmartDriveMotor frontLeft;
    private static SmartDriveMotor frontRight;
    private static SmartDriveMotor backLeft;
    private static SmartDriveMotor backRight;
    private static SmartDriveMotor leftDrive;
    private static SmartDriveMotor rightDrive;
    private static IMU imu;

    @ExportToBlocks(
            heading = "No-Odo",
            comment = "Call this once during init. 0 = Mecanum (frontLeft, frontRight, backLeft, backRight). "
                    + "1 = Tank (leftDrive, rightDrive). Maps motors, sets BRAKE, and initializes the Control Hub IMU.",
            tooltip = "Map mecanum (0) or tank (1) drive hardware",
            parameterLabels = {"Drive Type (0=Mecanum, 1=Tank)"},
            parameterDefaultValues = {"0"}
    )
    public static void initializeDrive(int driveType) {
        NodoBlocksFramework.driveType = (driveType == DRIVE_TYPE_TANK)
                ? DRIVE_TYPE_TANK
                : DRIVE_TYPE_MECANUM;
        chassis = null;
        frontLeft = frontRight = backLeft = backRight = null;
        leftDrive = rightDrive = null;
        imu = null;
        driveInitialized = false;

        if (hardwareMap == null) {
            addLine("initializeDrive: hardwareMap is null. Call this from a Blocks OpMode.");
            updateTelemetry();
            return;
        }

        if (NodoBlocksFramework.driveType == DRIVE_TYPE_TANK) {
            initializeTank();
        } else {
            initializeMecanum();
        }

        resetFieldHeading();
        driveInitialized = hasDriveMotors();
        addLine(driveInitialized
                ? "No-Odo drive ready (" + driveTypeName() + ")"
                : "No-Odo drive: no motors found. Check config names.");
        updateTelemetry();
    }

    @ExportToBlocks(
            heading = "No-Odo",
            comment = "Drives straight for durationSeconds at the given power (-1 to 1). "
                    + "Battery compensation (13V / current voltage) lives in SmartDriveMotor.setDrivePower, "
                    + "so the robot does not slow down as the pack sags. A P gyro loop holds the heading "
                    + "captured at the start of the move.",
            tooltip = "Timed gyro-straight drive with battery compensation",
            parameterLabels = {"Duration (seconds)", "Power"},
            parameterDefaultValues = {"1.0", "0.5"}
    )
    public static void driveStraight(double durationSeconds, double power) {
        if (!ensureInitialized("driveStraight")) {
            return;
        }
        if (durationSeconds <= 0) {
            stopDrive();
            return;
        }
        power = clamp(power, -1.0, 1.0);

        // Mecanum path reuses DriveStraightCommand: heading P-loop + SmartDriveMotor voltage scale.
        if (driveType == DRIVE_TYPE_MECANUM && chassis != null) {
            runBlocking(new DriveStraightCommand(chassis, power, Math.round(durationSeconds * 1000.0)));
            return;
        }

        double targetHeading = getHeading();
        ElapsedTime timer = new ElapsedTime();
        while (isOpModeActive() && timer.seconds() < durationSeconds) {
            // headingError > 0 means we need to rotate CCW; add to left / subtract from right.
            double headingError = normalizeAngle(targetHeading - getHeading());
            double correction = headingError * HEADING_P_GAIN;
            applyLeftRightPower(power - correction, power + correction);
        }
        stopDrive();
    }

    @ExportToBlocks(
            heading = "No-Odo",
            comment = "Pivots in place to an absolute IMU heading (degrees). Does not reset the IMU, "
                    + "so 0 is the heading from initializeDrive. Uses a PID loop on heading error, "
                    + "clamped to maxPower, and stops when within 2 degrees.",
            tooltip = "Gyro PID turn to an absolute heading",
            parameterLabels = {"Target Heading (degrees)", "Max Power"},
            parameterDefaultValues = {"90.0", "0.8"}
    )
    public static void turnToHeading(double targetHeading, double maxPower) {
        if (!ensureInitialized("turnToHeading")) {
            return;
        }
        if (chassis == null && imu == null) {
            addLine("turnToHeading: IMU not found, turn skipped.");
            updateTelemetry();
            return;
        }
        maxPower = Math.abs(maxPower);
        if (maxPower < 0.05) {
            maxPower = DEFAULT_MAX_TURN_POWER;
        }
        maxPower = Math.min(maxPower, 1.0);

        ElapsedTime pidTimer = new ElapsedTime();
        ElapsedTime settleTimer = new ElapsedTime();
        ElapsedTime timeout = new ElapsedTime();
        double previousError = headingError(targetHeading);
        double integral = 0.0;
        pidTimer.reset();
        settleTimer.reset();
        timeout.reset();

        while (isOpModeActive() && timeout.seconds() < TURN_TIMEOUT_SEC) {
            double error = headingError(targetHeading);
            double dt = pidTimer.seconds();
            pidTimer.reset();
            if (dt <= 0.0 || dt > 0.2) {
                dt = 0.02;
            }

            // Freeze I when far from the target so a long approach does not wind up.
            if (Math.abs(error) < 20.0) {
                integral += error * dt;
            } else {
                integral = 0.0;
            }
            double derivative = (error - previousError) / dt;
            previousError = error;

            double turnPower = (error * TURN_P_GAIN) + (integral * TURN_I_GAIN) + (derivative * TURN_D_GAIN);
            turnPower = clamp(turnPower, -maxPower, maxPower);
            applyLeftRightPower(turnPower, -turnPower);

            if (Math.abs(error) < HEADING_TOLERANCE_DEG) {
                if (settleTimer.milliseconds() >= TURN_SETTLE_MS) {
                    break;
                }
            } else {
                settleTimer.reset();
            }
        }
        stopDrive();
    }

    private static void initializeMecanum() {
        try {
            chassis = new NoOdoChassis(hardwareMap, DEFAULT_KF);
            frontLeft = chassis.getFrontLeft();
            frontRight = chassis.getFrontRight();
            backLeft = chassis.getBackLeft();
            backRight = chassis.getBackRight();
            addLine("Mapped mecanum via NoOdoChassis.");
        } catch (RuntimeException ignored) {
            chassis = null;
            frontLeft = mapMotor("frontLeft", "front_left");
            frontRight = mapMotor("frontRight", "front_right");
            backLeft = mapMotor("backLeft", "back_left");
            backRight = mapMotor("backRight", "back_right");
            if (frontRight != null) {
                frontRight.reverse();
            }
            if (backRight != null) {
                backRight.reverse();
            }
            initializeImu();
            addLine("Mecanum mapped individually (check names if a corner is missing).");
        }
    }

    private static void initializeTank() {
        leftDrive = mapMotor("leftDrive", "left_drive", "left");
        rightDrive = mapMotor("rightDrive", "right_drive", "right");
        if (rightDrive != null) {
            rightDrive.reverse();
        }
        initializeImu();
        addLine("Mapped tank motors leftDrive / rightDrive.");
    }

    private static void initializeImu() {
        if (hardwareMap == null) {
            return;
        }
        imu = hardwareMap.tryGet(IMU.class, "imu");
        if (imu == null) {
            addLine("IMU 'imu' not found. Gyro correction will be skipped.");
            return;
        }
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        )));
    }

    private static SmartDriveMotor mapMotor(String... names) {
        for (String name : names) {
            SmartDriveMotor motor = SmartDriveMotor.tryCreate(hardwareMap, name, DEFAULT_KF);
            if (motor != null) {
                return motor;
            }
        }
        addLine("Missing motor (tried " + names[0] + ").");
        return null;
    }

    /**
     * Runs a NoOdoCommand to completion while still honoring the driver's STOP button.
     */
    private static void runBlocking(NoOdoCommand command) {
        command.init();
        while (isOpModeActive() && !command.isFinished()) {
            command.execute();
        }
        command.end();
    }

    private static void applyLeftRightPower(double leftPower, double rightPower) {
        if (driveType == DRIVE_TYPE_TANK) {
            if (leftDrive != null) {
                leftDrive.setDrivePower(leftPower);
            }
            if (rightDrive != null) {
                rightDrive.setDrivePower(rightPower);
            }
            return;
        }
        if (chassis != null) {
            chassis.setMecanumPowers(leftPower, rightPower, leftPower, rightPower);
            return;
        }
        if (frontLeft != null) {
            frontLeft.setDrivePower(leftPower);
        }
        if (backLeft != null) {
            backLeft.setDrivePower(leftPower);
        }
        if (frontRight != null) {
            frontRight.setDrivePower(rightPower);
        }
        if (backRight != null) {
            backRight.setDrivePower(rightPower);
        }
    }

    private static void stopDrive() {
        applyLeftRightPower(0.0, 0.0);
    }

    private static double getHeading() {
        if (chassis != null) {
            return chassis.getHeading();
        }
        if (imu != null) {
            return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        }
        return 0.0;
    }

    private static void resetFieldHeading() {
        if (chassis != null) {
            chassis.resetYaw();
        } else if (imu != null) {
            imu.resetYaw();
        }
    }

    private static double headingError(double targetHeading) {
        return normalizeAngle(targetHeading - getHeading());
    }

    private static double normalizeAngle(double degrees) {
        while (degrees > 180.0) {
            degrees -= 360.0;
        }
        while (degrees < -180.0) {
            degrees += 360.0;
        }
        return degrees;
    }

    private static boolean ensureInitialized(String methodName) {
        if (driveInitialized && hasDriveMotors()) {
            return true;
        }
        addLine(methodName + ": call initializeDrive first.");
        updateTelemetry();
        return false;
    }

    private static boolean hasDriveMotors() {
        if (driveType == DRIVE_TYPE_TANK) {
            return leftDrive != null || rightDrive != null;
        }
        return chassis != null
                || frontLeft != null || frontRight != null
                || backLeft != null || backRight != null;
    }

    private static boolean isOpModeActive() {
        if (linearOpMode != null) {
            return linearOpMode.opModeIsActive();
        }
        return opMode != null;
    }

    private static String driveTypeName() {
        return driveType == DRIVE_TYPE_TANK ? "tank" : "mecanum";
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void addLine(String message) {
        if (telemetry != null) {
            telemetry.addLine(message);
        }
    }

    private static void updateTelemetry() {
        if (telemetry != null) {
            telemetry.update();
        }
    }
}
