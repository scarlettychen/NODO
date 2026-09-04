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
 * Two-motor tank drivetrain with voltage/kF motors and IMU heading.
 *
 * <p>Commands / {@code NODOTankRoutine} are optional. Use {@link #setPowers} for
 * TeleOp, or {@link #driveFor} / {@link #turnBy} from a {@code LinearOpMode}
 * (pass {@code this::opModeIsActive}).
 */
public class NODOTankDrive {

    private static final double HEADING_GAIN = 0.02;

    private static String leftName = "leftDrive";
    private static String rightName = "rightDrive";
    private static String imuName = "imu";

    private static DcMotor.Direction leftDir = DcMotor.Direction.FORWARD;
    private static DcMotor.Direction rightDir = DcMotor.Direction.REVERSE;

    private final SmartDriveMotor left;
    private final SmartDriveMotor right;
    private final IMU imu;
    private final ImuHeadingFilter headingFilter = new ImuHeadingFilter();
    private final TurnPDGains turnPD = new TurnPDGains();

    /**
     * Call in {@code init()} before {@code new NODOTankDrive(...)} if config names differ.
     */
    public static void setMotorNames(String left, String right) {
        leftName = left;
        rightName = right;
    }

    /**
     * Call in {@code init()} before {@code new NODOTankDrive(...)} so directions match your wiring.
     * Default is left FORWARD, right REVERSE.
     */
    public static void setMotorDirections(DcMotor.Direction leftDirection, DcMotor.Direction rightDirection) {
        leftDir = leftDirection;
        rightDir = rightDirection;
    }

    public static void setImuName(String controlHubImu) {
        imuName = controlHubImu;
    }

    public NODOTankDrive(HardwareMap hwMap, double kF) {
        try {
            UsageTracker.ping(hwMap);
        } catch (Exception ignored) {
            // Usage ping must never prevent the drivetrain from initializing.
        }

        left = new SmartDriveMotor(hwMap, leftName, kF);
        right = new SmartDriveMotor(hwMap, rightName, kF);
        left.setDirection(leftDir);
        right.setDirection(rightDir);

        imu = hwMap.get(IMU.class, imuName);
        setControlHubOrientation(LogoFacingDirection.UP, UsbFacingDirection.FORWARD);
    }

    public void setControlHubOrientation(LogoFacingDirection logo, UsbFacingDirection usb) {
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(logo, usb)));
        headingFilter.resetTo(readRawYawDegrees());
    }

    public void applyMotorDirections(DcMotor.Direction leftDirection, DcMotor.Direction rightDirection) {
        setMotorDirections(leftDirection, rightDirection);
        left.setDirection(leftDir);
        right.setDirection(rightDir);
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
     * Sets static tile-friction feedforward ({@code kF}) on both drive motors.
     * Typical start: {@code 0.03}. Pivot turns disable feedforward automatically.
     */
    public void setFeedforward(double kF) {
        left.setKF(kF);
        right.setKF(kF);
    }

    public SmartDriveMotor getLeft() {
        return left;
    }

    public SmartDriveMotor getRight() {
        return right;
    }

    public void resetYaw() {
        imu.resetYaw();
        headingFilter.resetTo(0.0);
    }

    public double getHeading() {
        return headingFilter.update(readRawYawDegrees());
    }

    public double getLastHeading() {
        return headingFilter.getHeading();
    }

    public double getRawHeading() {
        return readRawYawDegrees();
    }

    public double getYawVelocityDegreesPerSec() {
        return imu.getRobotAngularVelocity(AngleUnit.DEGREES).zRotationRate;
    }

    public void setPowers(double leftPower, double rightPower) {
        setPowers(leftPower, rightPower, true);
    }

    /**
     * @param applyFeedforward pass {@code false} for pivot turns (same reason as mecanum).
     */
    public void setPowers(double leftPower, double rightPower, boolean applyFeedforward) {
        left.setDrivePower(leftPower, applyFeedforward);
        right.setDrivePower(rightPower, applyFeedforward);
    }

    public void stop() {
        setPowers(0, 0, false);
    }

    /** One control tick of forward/back drive with gyro heading hold. */
    public void applyDriveHold(double power, double targetHeadingDeg) {
        double headingError = AngleMath.normalize(targetHeadingDeg - getRawHeading());
        double correction = headingError * HEADING_GAIN;
        setPowers(power - correction, power + correction, true);
    }

    /**
     * Blocking timed drive with heading hold. For {@code LinearOpMode}, pass
     * {@code this::opModeIsActive}.
     */
    public void driveFor(double power, long timeMs, BooleanSupplier isActive) {
        final double targetHeading = getRawHeading();
        BlockingLoops.driveFor(power, timeMs, isActive, new BlockingLoops.DriveTick() {
            @Override
            public void tick(double drivePower) {
                applyDriveHold(drivePower, targetHeading);
            }
        }, new Runnable() {
            @Override
            public void run() {
                stop();
            }
        });
    }

    /**
     * Blocking robot-relative turn. For {@code LinearOpMode}, pass
     * {@code this::opModeIsActive}.
     */
    public void turnBy(double relativeDegrees, BooleanSupplier isActive) {
        TankRelativeTurnController turn = new TankRelativeTurnController(this);
        turn.start(relativeDegrees);
        try {
            while (isActive.getAsBoolean() && !turn.update()) {
                BlockingLoops.yield();
            }
        } finally {
            turn.end();
        }
    }

    public void waitFor(long timeMs, BooleanSupplier isActive) {
        BlockingLoops.waitFor(timeMs, isActive);
    }

    private double readRawYawDegrees() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }
}
