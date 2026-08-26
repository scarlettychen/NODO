package com.nonodo.blocks;

import com.nonodo.UsageTracker;
import com.nonodo.hardware.NODOChassis;
import com.nonodo.hardware.NODOTankDrive;
import com.nonodo.hardware.SmartDriveMotor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.ExportClassToBlocks;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;

/**
 * Java-for-Blocks companion. Blocks never use the command framework — drive blocks
 * call the same chassis helpers as a LinearOpMode ({@code driveFor} / {@code strafeFor} /
 * {@code turnBy} / {@code waitFor}).
 *
 * <p>In init, call <b>one</b> of {@code initializeMecanumDrive} or {@code initializeTankDrive}
 * (or {@code initializeDrive} with {@link DRIVE_TYPE#MECANUM()} / {@link DRIVE_TYPE#TANK()}),
 * then {@code setControlHubOrientation}, then move blocks.
 *
 * <p>Mecanum and tank both use the same voltage compensation, raw-yaw gyro hold,
 * and robot-relative PD turns. Strafe is mecanum-only.
 */
@ExportClassToBlocks
public class NodoBlocksFramework extends BlocksOpModeCompanion {

    public static final int DRIVE_TYPE_MECANUM = 0;
    public static final int DRIVE_TYPE_TANK = 1;

    private static final double DEFAULT_KF = 0.03;

    private static int driveType = DRIVE_TYPE_MECANUM;
    private static boolean driveInitialized;

    private static NODOChassis chassis;
    private static NODOTankDrive tankDrive;
    private static SmartDriveMotor frontLeft;
    private static SmartDriveMotor frontRight;
    private static SmartDriveMotor backLeft;
    private static SmartDriveMotor backRight;
    private static SmartDriveMotor leftDrive;
    private static SmartDriveMotor rightDrive;
    private static IMU imu;

    @ExportToBlocks(
            heading = "NODO",
            comment = "Choose mecanum: maps frontLeft, frontRight, backLeft, backRight + Control Hub IMU. "
                    + "Call once in init. Do not also call initializeTankDrive.",
            tooltip = "Init mecanum drive"
    )
    public static void initializeMecanumDrive() {
        initializeDriveInternal(DRIVE_TYPE_MECANUM);
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Choose tank: maps leftDrive, rightDrive + Control Hub IMU. "
                    + "Call once in init. Do not also call initializeMecanumDrive. Strafe block will not work.",
            tooltip = "Init tank drive"
    )
    public static void initializeTankDrive() {
        initializeDriveInternal(DRIVE_TYPE_TANK);
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Choose drive type by name: MECANUM or TANK (case-insensitive). "
                    + "Prefer initializeMecanumDrive / initializeTankDrive if those blocks are clearer. "
                    + "Also accepts 0 = mecanum, 1 = tank for older programs.",
            tooltip = "Init drive: MECANUM or TANK",
            parameterLabels = {"Drive Type (MECANUM or TANK)"},
            parameterDefaultValues = {"MECANUM"}
    )
    public static void initializeDrive(String driveTypeName) {
        initializeDriveInternal(parseDriveType(driveTypeName));
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Initialize drive by type. Pass DRIVE_TYPE.MECANUM or DRIVE_TYPE.TANK constant blocks, "
                    + "or 0 = mecanum / 1 = tank. Prefer initializeMecanumDrive / initializeTankDrive if simpler.",
            tooltip = "Initialize drive (DRIVE_TYPE constant)",
            parameterLabels = {"Drive Type"},
            parameterDefaultValues = {"0"}
    )
    public static void initializeDrive(int driveType) {
        initializeDriveInternal(driveType == DRIVE_TYPE_TANK ? DRIVE_TYPE_TANK : DRIVE_TYPE_MECANUM);
    }

    private static void initializeDriveInternal(int type) {
        driveType = (type == DRIVE_TYPE_TANK) ? DRIVE_TYPE_TANK : DRIVE_TYPE_MECANUM;
        chassis = null;
        tankDrive = null;
        frontLeft = frontRight = backLeft = backRight = null;
        leftDrive = rightDrive = null;
        imu = null;
        driveInitialized = false;
        try {
            UsageTracker.ping(hardwareMap);
        } catch (Exception ignored) {
            // Usage ping must never prevent drive init.
        }

        if (hardwareMap == null) {
            addLine("initializeDrive: hardwareMap is null. Call this from a Blocks OpMode.");
            updateTelemetry();
            return;
        }

        if (driveType == DRIVE_TYPE_TANK) {
            initializeTank();
        } else {
            initializeMecanum();
        }

        resetFieldHeading();
        driveInitialized = hasDriveMotors();
        addLine(driveInitialized
                ? "NODO drive ready (" + driveTypeName() + ")"
                : "NODO drive: no motors found. Check config names.");
        updateTelemetry();
    }

    private static int parseDriveType(String driveTypeName) {
        if (driveTypeName == null || driveTypeName.trim().isEmpty()) {
            addLine("initializeDrive: empty type — defaulting to MECANUM");
            updateTelemetry();
            return DRIVE_TYPE_MECANUM;
        }
        String normalized = driveTypeName.trim().toUpperCase();
        if ("TANK".equals(normalized) || "1".equals(normalized)) {
            return DRIVE_TYPE_TANK;
        }
        if ("MECANUM".equals(normalized) || "0".equals(normalized)) {
            return DRIVE_TYPE_MECANUM;
        }
        addLine("initializeDrive: unknown '" + driveTypeName + "' — use MECANUM or TANK (default MECANUM)");
        updateTelemetry();
        return DRIVE_TYPE_MECANUM;
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Required. Sets Control Hub IMU orientation after you choose mecanum or tank init. "
                    + "Logo and USB are direction names: UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT. "
                    + "Wrong values break gyro heading.",
            tooltip = "Set Control Hub logo + USB facing",
            parameterLabels = {"Logo Facing", "USB Facing"},
            parameterDefaultValues = {"UP", "FORWARD"}
    )
    public static void setControlHubOrientation(String logoFacing, String usbFacing) {
        if (!ensureInitialized("setControlHubOrientation")) {
            return;
        }
        LogoFacingDirection logo;
        UsbFacingDirection usb;
        try {
            logo = parseLogo(logoFacing);
            usb = parseUsb(usbFacing);
        } catch (IllegalArgumentException e) {
            addLine("setControlHubOrientation: " + e.getMessage());
            updateTelemetry();
            return;
        }

        if (chassis != null) {
            chassis.setControlHubOrientation(logo, usb);
        } else if (tankDrive != null) {
            tankDrive.setControlHubOrientation(logo, usb);
        } else if (imu != null) {
            imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(logo, usb)));
            imu.resetYaw();
        } else {
            addLine("setControlHubOrientation: no IMU available.");
            updateTelemetry();
            return;
        }
        addLine("Control Hub orientation: logo=" + logo + " usb=" + usb);
        updateTelemetry();
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Optional. Sets Expansion Hub IMU orientation (default hardware name imu2). "
                    + "No-op if that IMU is missing. Same direction names as Control Hub: "
                    + "UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT.",
            tooltip = "Set Expansion Hub logo + USB facing (optional)",
            parameterLabels = {"Logo Facing", "USB Facing"},
            parameterDefaultValues = {"UP", "FORWARD"}
    )
    public static void setExpansionHubOrientation(String logoFacing, String usbFacing) {
        if (!ensureInitialized("setExpansionHubOrientation")) {
            return;
        }
        if (chassis == null) {
            addLine("setExpansionHubOrientation: mecanum chassis required (skipped).");
            updateTelemetry();
            return;
        }
        LogoFacingDirection logo;
        UsbFacingDirection usb;
        try {
            logo = parseLogo(logoFacing);
            usb = parseUsb(usbFacing);
        } catch (IllegalArgumentException e) {
            addLine("setExpansionHubOrientation: " + e.getMessage());
            updateTelemetry();
            return;
        }

        chassis.setExpansionHubOrientation(logo, usb);
        addLine("Expansion Hub orientation: logo=" + logo + " usb=" + usb);
        updateTelemetry();
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Optional. Sets turn PD gains after init. Defaults are kP=0.035, kD=0.002, "
                    + "maxPower=0.8. Call before turnToHeading. Higher kP = snappier; higher kD = more damping.",
            tooltip = "Set turn PD gains + max power",
            parameterLabels = {"kP", "kD", "Max Power"},
            parameterDefaultValues = {"0.035", "0.002", "0.8"}
    )
    public static void setTurnPD(double kP, double kD, double maxPower) {
        if (!ensureInitialized("setTurnPD")) {
            return;
        }
        if (chassis != null) {
            chassis.setTurnPD(kP, kD, maxPower);
        } else if (tankDrive != null) {
            tankDrive.setTurnPD(kP, kD, maxPower);
        } else {
            addLine("setTurnPD: drive not ready.");
            updateTelemetry();
            return;
        }
        addLine("Turn PD: kP=" + kP + " kD=" + kD + " max=" + maxPower);
        updateTelemetry();
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Optional. Sets static feedforward (kF) for foam-tile friction on all drive motors. "
                    + "Default at init is 0.03. Typical range 0.02–0.05. Too high = twitchy near zero; "
                    + "too low = sluggish starts. Call after initializeMecanumDrive / initializeTankDrive.",
            tooltip = "Set drive feedforward (kF)",
            parameterLabels = {"Feedforward (kF)"},
            parameterDefaultValues = {"0.03"}
    )
    public static void setFeedforward(double kF) {
        if (!ensureInitialized("setFeedforward")) {
            return;
        }
        if (chassis != null) {
            chassis.setFeedforward(kF);
        } else if (tankDrive != null) {
            tankDrive.setFeedforward(kF);
        } else {
            applyFeedforwardToMappedMotors(kF);
        }
        addLine("Feedforward kF=" + kF);
        updateTelemetry();
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Drives straight for durationSeconds at the given power (-1 to 1). "
                    + "Battery compensation lives in SmartDriveMotor. A P gyro loop holds the heading "
                    + "captured at the start of the move (same for mecanum and tank).",
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
        long timeMs = Math.round(durationSeconds * 1000.0);

        if (driveType == DRIVE_TYPE_TANK && tankDrive != null) {
            tankDrive.driveFor(power, timeMs, NodoBlocksFramework::isOpModeActive);
            return;
        }
        if (chassis != null) {
            chassis.driveFor(power, timeMs, NodoBlocksFramework::isOpModeActive);
            return;
        }

        addLine("driveStraight: drive hardware missing.");
        updateTelemetry();
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Strafes sideways for durationSeconds at the given power (-1 to 1). "
                    + "Positive = right, negative = left. Gyro holds heading. Mecanum only.",
            tooltip = "Timed gyro-hold strafe (mecanum)",
            parameterLabels = {"Duration (seconds)", "Power"},
            parameterDefaultValues = {"0.6", "0.5"}
    )
    public static void strafe(double durationSeconds, double power) {
        if (!ensureInitialized("strafe")) {
            return;
        }
        if (durationSeconds <= 0) {
            stopDrive();
            return;
        }
        power = clamp(power, -1.0, 1.0);

        if (driveType != DRIVE_TYPE_MECANUM || chassis == null) {
            addLine("strafe: mecanum only (tank cannot strafe).");
            updateTelemetry();
            return;
        }

        chassis.strafeFor(power, Math.round(durationSeconds * 1000.0), NodoBlocksFramework::isOpModeActive);
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Pauses the OpMode for durationSeconds with motors stopped. "
                    + "Use between drive/strafe/turn blocks.",
            tooltip = "Wait / pause",
            parameterLabels = {"Duration (seconds)"},
            parameterDefaultValues = {"0.5"}
    )
    public static void waitSeconds(double durationSeconds) {
        if (durationSeconds <= 0) {
            return;
        }
        long timeMs = Math.round(durationSeconds * 1000.0);
        if (chassis != null) {
            chassis.waitFor(timeMs, NodoBlocksFramework::isOpModeActive);
        } else if (tankDrive != null) {
            tankDrive.waitFor(timeMs, NodoBlocksFramework::isOpModeActive);
        } else {
            ElapsedTime timer = new ElapsedTime();
            while (isOpModeActive() && timer.milliseconds() < timeMs) {
                // idle
            }
        }
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Pivots in place by a robot-relative angle (degrees) from the current heading. "
                    + "Positive = CCW / +yaw, negative = CW. Mecanum and tank use the same PD turn "
                    + "(no static kF). Max Power is unused for PD (kept for Blocks compatibility).",
            tooltip = "Gyro relative PD turn (mecanum + tank)",
            parameterLabels = {"Turn Degrees (relative)", "Max Power"},
            parameterDefaultValues = {"90.0", "0.8"}
    )
    public static void turnToHeading(double turnDegrees, double maxPower) {
        if (!ensureInitialized("turnToHeading")) {
            return;
        }

        if (driveType == DRIVE_TYPE_TANK && tankDrive != null) {
            tankDrive.turnBy(turnDegrees, NodoBlocksFramework::isOpModeActive);
            return;
        }
        if (chassis != null) {
            chassis.turnBy(turnDegrees, NodoBlocksFramework::isOpModeActive);
            return;
        }

        addLine("turnToHeading: IMU / drive not found, turn skipped.");
        updateTelemetry();
    }

    private static void initializeMecanum() {
        try {
            chassis = new NODOChassis(hardwareMap, DEFAULT_KF);
            frontLeft = chassis.getFrontLeft();
            frontRight = chassis.getFrontRight();
            backLeft = chassis.getBackLeft();
            backRight = chassis.getBackRight();
            addLine("Mapped mecanum via NODOChassis.");
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
        try {
            tankDrive = new NODOTankDrive(hardwareMap, DEFAULT_KF);
            leftDrive = tankDrive.getLeft();
            rightDrive = tankDrive.getRight();
            addLine("Mapped tank via NODOTankDrive (leftDrive / rightDrive).");
        } catch (RuntimeException e) {
            tankDrive = null;
            leftDrive = mapMotor("leftDrive", "left_drive", "left");
            rightDrive = mapMotor("rightDrive", "right_drive", "right");
            if (rightDrive != null) {
                rightDrive.reverse();
            }
            initializeImu();
            addLine("Tank mapped individually: " + e.getMessage());
        }
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

    private static void applyFeedforwardToMappedMotors(double kF) {
        if (frontLeft != null) {
            frontLeft.setKF(kF);
        }
        if (frontRight != null) {
            frontRight.setKF(kF);
        }
        if (backLeft != null) {
            backLeft.setKF(kF);
        }
        if (backRight != null) {
            backRight.setKF(kF);
        }
        if (leftDrive != null) {
            leftDrive.setKF(kF);
        }
        if (rightDrive != null) {
            rightDrive.setKF(kF);
        }
    }

    private static void stopDrive() {
        if (tankDrive != null) {
            tankDrive.stop();
            return;
        }
        if (chassis != null) {
            chassis.stop();
            return;
        }
        if (leftDrive != null) {
            leftDrive.setDrivePower(0, false);
        }
        if (rightDrive != null) {
            rightDrive.setDrivePower(0, false);
        }
        if (frontLeft != null) {
            frontLeft.setDrivePower(0, false);
        }
        if (frontRight != null) {
            frontRight.setDrivePower(0, false);
        }
        if (backLeft != null) {
            backLeft.setDrivePower(0, false);
        }
        if (backRight != null) {
            backRight.setDrivePower(0, false);
        }
    }

    private static void resetFieldHeading() {
        if (tankDrive != null) {
            tankDrive.resetYaw();
        } else if (chassis != null) {
            chassis.resetYaw();
        } else if (imu != null) {
            imu.resetYaw();
        }
    }

    private static boolean ensureInitialized(String methodName) {
        if (driveInitialized && hasDriveMotors()) {
            return true;
        }
        addLine(methodName + ": call initializeMecanumDrive or initializeTankDrive first.");
        updateTelemetry();
        return false;
    }

    private static boolean hasDriveMotors() {
        if (driveType == DRIVE_TYPE_TANK) {
            return tankDrive != null || leftDrive != null || rightDrive != null;
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

    private static LogoFacingDirection parseLogo(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("logo facing is empty");
        }
        try {
            return LogoFacingDirection.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "bad logo '" + value + "' (use UP DOWN FORWARD BACKWARD LEFT RIGHT)"
            );
        }
    }

    private static UsbFacingDirection parseUsb(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("USB facing is empty");
        }
        try {
            return UsbFacingDirection.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "bad USB '" + value + "' (use UP DOWN FORWARD BACKWARD LEFT RIGHT)"
            );
        }
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
