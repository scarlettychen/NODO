package com.nonodo.hardware;

import com.nonodo.UsageTracker;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.function.BooleanSupplier;

/**
 * Mecanum drivetrain with voltage/kF motors and Control Hub IMU heading.
 *
 * <p>Commands / {@code NODORoutine} are optional. Teams can drive with
 * {@link #setMecanumPowers} (TeleOp) or the blocking helpers
 * {@link #driveFor}, {@link #strafeFor}, {@link #turnBy} from a
 * {@code LinearOpMode} (pass {@code this::opModeIsActive}).
 */
public class NODOChassis {

    private static final double HEADING_GAIN = 0.02;

    private static String frontLeftName = "frontLeft";
    private static String frontRightName = "frontRight";
    private static String backLeftName = "backLeft";
    private static String backRightName = "backRight";
    private static String controlHubImuName = "imu";
    private static String expansionHubImuName = "imu2";

    private final SmartDriveMotor frontLeft;
    private final SmartDriveMotor frontRight;
    private final SmartDriveMotor backLeft;
    private final SmartDriveMotor backRight;
    private final IMU controlHubImu;
    private final IMU expansionHubImu;
    private final ImuHeadingFilter headingFilter = new ImuHeadingFilter();
    private final TurnPDGains turnPD = new TurnPDGains();

    /**
     * Call this in {@code init()} before {@code new NODOChassis(...)} so the
     * hardware map names match your robot configuration.
     */
    public static void setMotorNames(String frontLeft, String frontRight, String backLeft, String backRight) {
        frontLeftName = frontLeft;
        frontRightName = frontRight;
        backLeftName = backLeft;
        backRightName = backRight;
    }

    /**
     * Optional. Defaults are {@code imu} (Control Hub) and {@code imu2} (Expansion Hub).
     */
    public static void setImuNames(String controlHubImu, String expansionHubImu) {
        controlHubImuName = controlHubImu;
        expansionHubImuName = expansionHubImu;
    }

    public NODOChassis(HardwareMap hwMap, double kF) {
        try {
            UsageTracker.ping(hwMap);
        } catch (Exception ignored) {
            // Usage ping must never prevent the drivetrain from initializing.
        }

        // kF is the static feedforward needed to overcome foam tile friction.
        frontLeft = new SmartDriveMotor(hwMap, frontLeftName, kF);
        frontRight = new SmartDriveMotor(hwMap, frontRightName, kF);
        backLeft = new SmartDriveMotor(hwMap, backLeftName, kF);
        backRight = new SmartDriveMotor(hwMap, backRightName, kF);

        frontRight.reverse();
        backRight.reverse();

        controlHubImu = hwMap.get(IMU.class, controlHubImuName);
        expansionHubImu = hwMap.tryGet(IMU.class, expansionHubImuName);

        // Control Hub IMU is required for heading; Expansion Hub is configured separately if present.
        setControlHubOrientation(LogoFacingDirection.UP, UsbFacingDirection.FORWARD);
    }

    /**
     * Required. Call from {@code init()} after constructing the chassis.
     * Heading / gyro correction use the Control Hub IMU.
     */
    public void setControlHubOrientation(LogoFacingDirection logo, UsbFacingDirection usb) {
        controlHubImu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(logo, usb)
        ));
        headingFilter.resetTo(readRawYawDegrees());
    }

    /**
     * Optional. Call only if you have an Expansion Hub IMU mapped (default name {@code imu2}).
     * No-ops when that IMU is missing.
     */
    public void setExpansionHubOrientation(LogoFacingDirection logo, UsbFacingDirection usb) {
        if (expansionHubImu == null) {
            return;
        }
        expansionHubImu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(logo, usb)
        ));
    }

    /**
     * Call from {@code init()} after constructing the chassis.
     * Order is frontLeft, frontRight, backLeft, backRight.
     * Default is left FORWARD, right REVERSE.
     */
    public void setMotorDirections(
            DcMotor.Direction frontLeftDir,
            DcMotor.Direction frontRightDir,
            DcMotor.Direction backLeftDir,
            DcMotor.Direction backRightDir
    ) {
        frontLeft.setDirection(frontLeftDir);
        frontRight.setDirection(frontRightDir);
        backLeft.setDirection(backLeftDir);
        backRight.setDirection(backRightDir);
    }

    /** Live turn PD settings used by {@link #turnBy} and turn commands. */
    public TurnPDGains getTurnPD() {
        return turnPD;
    }

    /** Sets turn proportional and derivative gains (defaults 0.035 / 0.002). */
    public void setTurnPD(double kP, double kD) {
        turnPD.setPD(kP, kD);
    }

    /** Sets turn PD gains and max motor power clamp (default max 0.8). */
    public void setTurnPD(double kP, double kD, double maxPower) {
        turnPD.setPD(kP, kD).setMaxPower(maxPower);
    }

    public void setTurnToleranceDegrees(double toleranceDegrees) {
        turnPD.setToleranceDegrees(toleranceDegrees);
    }

    public void setTurnSettleMs(double settleMs) {
        turnPD.setSettleMs(settleMs);
    }

    /**
     * Sets static tile-friction feedforward ({@code kF}) on all drive motors.
     * Typical start: {@code 0.03}. Pivot turns disable feedforward automatically.
     */
    public void setFeedforward(double kF) {
        frontLeft.setKF(kF);
        frontRight.setKF(kF);
        backLeft.setKF(kF);
        backRight.setKF(kF);
    }

    public SmartDriveMotor getFrontLeft() {
        return frontLeft;
    }

    public SmartDriveMotor getFrontRight() {
        return frontRight;
    }

    public SmartDriveMotor getBackLeft() {
        return backLeft;
    }

    public SmartDriveMotor getBackRight() {
        return backRight;
    }

    public void resetYaw() {
        controlHubImu.resetYaw();
        headingFilter.resetTo(0.0);
    }

    /**
     * Low-pass filtered Control Hub yaw (degrees). Prefer this for drive hold loops.
     * Each call advances the filter once — avoid calling it multiple times per loop
     * if you also need the same sample for control (use {@link #getLastHeading()}).
     */
    public double getHeading() {
        return headingFilter.update(readRawYawDegrees());
    }

    /**
     * Last filtered heading without sampling the IMU again.
     */
    public double getLastHeading() {
        return headingFilter.getHeading();
    }

    /**
     * Unfiltered IMU yaw. Prefer this for turn-to-heading control (no filter lag).
     */
    public double getRawHeading() {
        return readRawYawDegrees();
    }

    public double getYaw() {
        return getHeading();
    }

    private double readRawYawDegrees() {
        return controlHubImu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    /**
     * Robot-frame yaw rate from the Control Hub IMU (degrees/sec).
     * Positive matches increasing {@link #getRawHeading()} (typically CCW).
     * Uses {@code IMU.getRobotAngularVelocity(DEGREES).zRotationRate}.
     */
    public double getYawVelocityDegreesPerSec() {
        return controlHubImu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;
    }

    public void setMecanumPowers(double fl, double fr, double bl, double br) {
        setMecanumPowers(fl, fr, bl, br, true);
    }

    /**
     * @param applyFeedforward pass {@code false} for pivot turns so static {@code kF}
     *                         does not bang-bang near the heading target.
     */
    public void setMecanumPowers(double fl, double fr, double bl, double br, boolean applyFeedforward) {
        frontLeft.setDrivePower(fl, applyFeedforward);
        frontRight.setDrivePower(fr, applyFeedforward);
        backLeft.setDrivePower(bl, applyFeedforward);
        backRight.setDrivePower(br, applyFeedforward);
    }

    /** Stops all drive motors (no feedforward). */
    public void stop() {
        setMecanumPowers(0, 0, 0, 0, false);
    }

    /**
     * One control tick of forward/back drive with gyro heading hold.
     * Lock {@code targetHeadingDeg} at the start of the move (usually {@link #getRawHeading()}).
     */
    public void applyDriveHold(double power, double targetHeadingDeg) {
        double headingError = AngleMath.normalize(targetHeadingDeg - getRawHeading());
        double correction = headingError * HEADING_GAIN;
        setMecanumPowers(
                power - correction,
                power + correction,
                power - correction,
                power + correction
        );
    }

    /**
     * One control tick of strafe with gyro heading hold.
     * Positive {@code power} = right, negative = left.
     */
    public void applyStrafeHold(double power, double targetHeadingDeg) {
        double headingError = AngleMath.normalize(targetHeadingDeg - getRawHeading());
        double correction = headingError * HEADING_GAIN;
        setMecanumPowers(
                power + correction,
                -power - correction,
                -power + correction,
                power - correction
        );
    }

    /**
     * Blocking timed drive with heading hold. For {@code LinearOpMode}, pass
     * {@code this::opModeIsActive}. Does not use the command framework.
     */
    public void driveFor(double power, long timeMs, BooleanSupplier isActive) {
        double targetHeading = getRawHeading();
        ElapsedTime timer = new ElapsedTime();
        while (isActive.getAsBoolean() && timer.milliseconds() < timeMs) {
            applyDriveHold(power, targetHeading);
        }
        stop();
    }

    /**
     * Blocking timed strafe with heading hold. Positive power = right.
     * For {@code LinearOpMode}, pass {@code this::opModeIsActive}.
     */
    public void strafeFor(double power, long timeMs, BooleanSupplier isActive) {
        double targetHeading = getRawHeading();
        ElapsedTime timer = new ElapsedTime();
        while (isActive.getAsBoolean() && timer.milliseconds() < timeMs) {
            applyStrafeHold(power, targetHeading);
        }
        stop();
    }

    /**
     * Blocking robot-relative turn (degrees from current heading).
     * For {@code LinearOpMode}, pass {@code this::opModeIsActive}.
     */
    public void turnBy(double relativeDegrees, BooleanSupplier isActive) {
        RelativeTurnController turn = new RelativeTurnController(this);
        turn.start(relativeDegrees);
        while (isActive.getAsBoolean() && !turn.update()) {
            // PD turn ticks until settled or OpMode stops.
        }
        turn.end();
    }

    /**
     * Blocking wait that still yields while {@code isActive} is true.
     * For {@code LinearOpMode}, pass {@code this::opModeIsActive}.
     */
    public void waitFor(long timeMs, BooleanSupplier isActive) {
        ElapsedTime timer = new ElapsedTime();
        while (isActive.getAsBoolean() && timer.milliseconds() < timeMs) {
            // idle
        }
    }
}
