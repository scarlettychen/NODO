package com.nonodo.blocks;

import com.nonodo.hardware.BlockingLoops;
import com.nonodo.hardware.NODOChassis;
import com.nonodo.hardware.NODOTankDrive;
import com.nonodo.hardware.SmartDriveMotor;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Shared Blocks state and helpers. Use {@link NodoBlocksInit} and {@link NodoBlocksMotion}
 * for exported toolbox blocks.
 */
final class NodoBlocksRuntime {

    static final int DRIVE_TYPE_MECANUM = 0;
    static final int DRIVE_TYPE_TANK = 1;

    private static final double DEFAULT_KF = 0.03;

    private static double feedforwardKf = DEFAULT_KF;
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

    private static String mecanumFlName = "frontLeft";
    private static String mecanumFrName = "frontRight";
    private static String mecanumBlName = "backLeft";
    private static String mecanumBrName = "backRight";
    private static DcMotor.Direction mecanumFlDir = DcMotor.Direction.FORWARD;
    private static DcMotor.Direction mecanumFrDir = DcMotor.Direction.REVERSE;
    private static DcMotor.Direction mecanumBlDir = DcMotor.Direction.FORWARD;
    private static DcMotor.Direction mecanumBrDir = DcMotor.Direction.REVERSE;

    private static String tankLeftName = "leftDrive";
    private static String tankRightName = "rightDrive";
    private static DcMotor.Direction tankLeftDir = DcMotor.Direction.FORWARD;
    private static DcMotor.Direction tankRightDir = DcMotor.Direction.REVERSE;

    private static long completedTimedDriveKey = Long.MIN_VALUE;

    private NodoBlocksRuntime() {
    }

    static void setMecanum(
            String frontLeftName,
            String frontRightName,
            String backLeftName,
            String backRightName,
            String frontLeftDirection,
            String frontRightDirection,
            String backLeftDirection,
            String backRightDirection
    ) {
        mecanumFlName = requireName(frontLeftName, "frontLeft");
        mecanumFrName = requireName(frontRightName, "frontRight");
        mecanumBlName = requireName(backLeftName, "backLeft");
        mecanumBrName = requireName(backRightName, "backRight");
        try {
            mecanumFlDir = parseDirection(frontLeftDirection);
            mecanumFrDir = parseDirection(frontRightDirection);
            mecanumBlDir = parseDirection(backLeftDirection);
            mecanumBrDir = parseDirection(backRightDirection);
        } catch (IllegalArgumentException e) {
            addLine("setMecanum: " + e.getMessage());
            updateTelemetry();
            return;
        }
        NODOChassis.setMotorNames(mecanumFlName, mecanumFrName, mecanumBlName, mecanumBrName);
        NODOChassis.setMotorDirections(mecanumFlDir, mecanumFrDir, mecanumBlDir, mecanumBrDir);
        addLine("Mecanum: " + mecanumFlName + "/" + mecanumFrName + "/" + mecanumBlName + "/"
                + mecanumBrName);
        updateTelemetry();
    }

    static void setTank(
            String leftName,
            String rightName,
            String leftDirection,
            String rightDirection
    ) {
        tankLeftName = requireName(leftName, "leftDrive");
        tankRightName = requireName(rightName, "rightDrive");
        try {
            tankLeftDir = parseDirection(leftDirection);
            tankRightDir = parseDirection(rightDirection);
        } catch (IllegalArgumentException e) {
            addLine("setTank: " + e.getMessage());
            updateTelemetry();
            return;
        }
        NODOTankDrive.setMotorNames(tankLeftName, tankRightName);
        NODOTankDrive.setMotorDirections(tankLeftDir, tankRightDir);
        addLine("Tank: " + tankLeftName + "/" + tankRightName);
        updateTelemetry();
    }

    static void initializeMecanumDrive() {
        initializeDriveInternal(DRIVE_TYPE_MECANUM);
    }

    static void initializeTankDrive() {
        initializeDriveInternal(DRIVE_TYPE_TANK);
    }

    static void setControlHubOrientation(String logoFacing, String usbFacing) {
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

    static void setExpansionHubOrientation(String logoFacing, String usbFacing) {
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

    static void setFeedforward(double kF) {
        feedforwardKf = kF;
        if (driveInitialized && hasDriveMotors()) {
            if (chassis != null) {
                chassis.setFeedforward(kF);
            } else if (tankDrive != null) {
                tankDrive.setFeedforward(kF);
            } else {
                applyFeedforwardToMappedMotors(kF);
            }
        }
        addLine("Feedforward kF=" + kF);
        updateTelemetry();
    }

    static void setTurnPD(double kP, double kD) {
        if (!ensureInitialized("setTurnPD")) {
            return;
        }
        if (chassis != null) {
            chassis.setTurnPD(kP, kD);
        } else if (tankDrive != null) {
            tankDrive.setTurnPD(kP, kD);
        } else {
            addLine("setTurnPD: drive not ready.");
            updateTelemetry();
            return;
        }
        addLine("Turn PD: kP=" + kP + " kD=" + kD);
        updateTelemetry();
    }

    static void setTurnPD(double kP, double kD, double maxPower) {
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

    static void driveFor(double power, double durationMs) {
        if (!ensureInitialized("driveFor")) {
            return;
        }
        if (durationMs <= 0) {
            stopDrive();
            clearCompletedTimedMove();
            return;
        }
        power = clamp(power, -1.0, 1.0);
        long timeMs = Math.round(durationMs);
        long moveKey = timedDriveKey(power, timeMs);
        if (moveKey == completedTimedDriveKey) {
            stopDrive();
            return;
        }

        if (driveType == DRIVE_TYPE_TANK && tankDrive != null) {
            tankDrive.driveFor(power, timeMs, NodoBlocksRuntime::isOpModeActive);
        } else if (chassis != null) {
            chassis.driveFor(power, timeMs, NodoBlocksRuntime::isOpModeActive);
        } else {
            addLine("driveFor: drive hardware missing.");
            updateTelemetry();
            return;
        }
        completedTimedDriveKey = moveKey;
    }

    static void strafeFor(double power, double durationMs) {
        if (!ensureInitialized("strafeFor")) {
            return;
        }
        clearCompletedTimedMove();
        if (durationMs <= 0) {
            stopDrive();
            return;
        }
        power = clamp(power, -1.0, 1.0);

        if (driveType != DRIVE_TYPE_MECANUM || chassis == null) {
            addLine("strafeFor: mecanum only (tank cannot strafe).");
            updateTelemetry();
            return;
        }

        chassis.strafeFor(power, Math.round(durationMs), NodoBlocksRuntime::isOpModeActive);
    }

    static void turnBy(double turnDegrees) {
        if (!ensureInitialized("turnBy")) {
            return;
        }
        clearCompletedTimedMove();

        if (driveType == DRIVE_TYPE_TANK && tankDrive != null) {
            tankDrive.turnBy(turnDegrees, NodoBlocksRuntime::isOpModeActive);
            return;
        }
        if (chassis != null) {
            chassis.turnBy(turnDegrees, NodoBlocksRuntime::isOpModeActive);
            return;
        }

        addLine("turnBy: IMU / drive not found, turn skipped.");
        updateTelemetry();
    }

    static void waitFor(double durationMs) {
        clearCompletedTimedMove();
        if (durationMs <= 0) {
            return;
        }
        long timeMs = Math.round(durationMs);
        if (chassis != null) {
            chassis.waitFor(timeMs, NodoBlocksRuntime::isOpModeActive);
        } else if (tankDrive != null) {
            tankDrive.waitFor(timeMs, NodoBlocksRuntime::isOpModeActive);
        } else {
            BlockingLoops.waitFor(timeMs, NodoBlocksRuntime::isOpModeActive);
        }
    }

    static double getHeading() {
        if (tankDrive != null) {
            return tankDrive.getHeading();
        }
        if (chassis != null) {
            return chassis.getHeading();
        }
        if (imu != null) {
            return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
        }
        return 0.0;
    }

    static void stopDriveMotors() {
        stopDrive();
        clearCompletedTimedMove();
        addLine("Drive motors stopped.");
        updateTelemetry();
    }

    private static void initializeDriveInternal(int type) {
        driveType = (type == DRIVE_TYPE_TANK) ? DRIVE_TYPE_TANK : DRIVE_TYPE_MECANUM;
        clearCompletedTimedMove();
        chassis = null;
        tankDrive = null;
        frontLeft = frontRight = backLeft = backRight = null;
        leftDrive = rightDrive = null;
        imu = null;
        driveInitialized = false;

        if (BlocksOpModeCompanion.hardwareMap == null) {
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

    private static void initializeMecanum() {
        NODOChassis.setMotorNames(mecanumFlName, mecanumFrName, mecanumBlName, mecanumBrName);
        NODOChassis.setMotorDirections(mecanumFlDir, mecanumFrDir, mecanumBlDir, mecanumBrDir);
        try {
            chassis = new NODOChassis(BlocksOpModeCompanion.hardwareMap, feedforwardKf);
            frontLeft = chassis.getFrontLeft();
            frontRight = chassis.getFrontRight();
            backLeft = chassis.getBackLeft();
            backRight = chassis.getBackRight();
            addLine("Mapped mecanum via NODOChassis.");
        } catch (RuntimeException ignored) {
            chassis = null;
            frontLeft = mapMotor(mecanumFlName);
            frontRight = mapMotor(mecanumFrName);
            backLeft = mapMotor(mecanumBlName);
            backRight = mapMotor(mecanumBrName);
            applyMecanumDirections(frontLeft, frontRight, backLeft, backRight);
            initializeImu();
            addLine("Mecanum mapped individually (check names if a corner is missing).");
        }
    }

    private static void initializeTank() {
        NODOTankDrive.setMotorNames(tankLeftName, tankRightName);
        NODOTankDrive.setMotorDirections(tankLeftDir, tankRightDir);
        try {
            tankDrive = new NODOTankDrive(BlocksOpModeCompanion.hardwareMap, feedforwardKf);
            leftDrive = tankDrive.getLeft();
            rightDrive = tankDrive.getRight();
            addLine("Mapped tank via NODOTankDrive.");
        } catch (RuntimeException e) {
            tankDrive = null;
            leftDrive = mapMotor(tankLeftName);
            rightDrive = mapMotor(tankRightName);
            applyTankDirections(leftDrive, rightDrive);
            initializeImu();
            addLine("Tank mapped individually: " + e.getMessage());
        }
    }

    private static void initializeImu() {
        if (BlocksOpModeCompanion.hardwareMap == null) {
            return;
        }
        imu = BlocksOpModeCompanion.hardwareMap.tryGet(IMU.class, "imu");
        if (imu == null) {
            addLine("IMU 'imu' not found. Gyro correction will be skipped.");
            return;
        }
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        )));
    }

    private static SmartDriveMotor mapMotor(String name) {
        SmartDriveMotor motor = SmartDriveMotor.tryCreate(
                BlocksOpModeCompanion.hardwareMap, name, feedforwardKf);
        if (motor == null) {
            addLine("Missing motor '" + name + "'.");
        }
        return motor;
    }

    private static void applyMecanumDirections(
            SmartDriveMotor fl,
            SmartDriveMotor fr,
            SmartDriveMotor bl,
            SmartDriveMotor br
    ) {
        if (fl != null) {
            fl.setDirection(mecanumFlDir);
        }
        if (fr != null) {
            fr.setDirection(mecanumFrDir);
        }
        if (bl != null) {
            bl.setDirection(mecanumBlDir);
        }
        if (br != null) {
            br.setDirection(mecanumBrDir);
        }
    }

    private static void applyTankDirections(SmartDriveMotor left, SmartDriveMotor right) {
        if (left != null) {
            left.setDirection(tankLeftDir);
        }
        if (right != null) {
            right.setDirection(tankRightDir);
        }
    }

    private static String requireName(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
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
        LinearOpMode linearOpMode = BlocksOpModeCompanion.linearOpMode;
        OpMode opMode = BlocksOpModeCompanion.opMode;
        if (linearOpMode != null) {
            return linearOpMode.opModeIsActive();
        }
        if (opMode instanceof LinearOpMode) {
            return ((LinearOpMode) opMode).opModeIsActive();
        }
        if (opMode != null) {
            return opMode.getRuntime() > 0 && !Thread.currentThread().isInterrupted();
        }
        return false;
    }

    private static long timedDriveKey(double power, long durationMs) {
        return Double.doubleToLongBits(power) ^ (durationMs * 31L);
    }

    private static void clearCompletedTimedMove() {
        completedTimedDriveKey = Long.MIN_VALUE;
    }

    private static String driveTypeName() {
        return driveType == DRIVE_TYPE_TANK ? "tank" : "mecanum";
    }

    private static DcMotor.Direction parseDirection(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("direction is empty");
        }
        try {
            return DcMotor.Direction.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "bad direction '" + value + "' (use FORWARD or REVERSE)"
            );
        }
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
        if (BlocksOpModeCompanion.telemetry != null) {
            BlocksOpModeCompanion.telemetry.addLine(message);
        }
    }

    private static void updateTelemetry() {
        if (BlocksOpModeCompanion.telemetry != null) {
            BlocksOpModeCompanion.telemetry.update();
        }
    }
}
