package com.nonodo.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class NoOdoChassis {

    private static final double HEADING_GAIN = 0.02;

    private final SmartDriveMotor frontLeft;
    private final SmartDriveMotor frontRight;
    private final SmartDriveMotor backLeft;
    private final SmartDriveMotor backRight;
    private final IMU imu;

    public NoOdoChassis(HardwareMap hwMap, double kF) {
        this(hwMap, kF,
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
    }

    public NoOdoChassis(
            HardwareMap hwMap,
            double kF,
            RevHubOrientationOnRobot.LogoFacingDirection logoDir,
            RevHubOrientationOnRobot.UsbFacingDirection usbDir
    ) {
        // kF is the static feedforward needed to overcome foam tile friction.
        frontLeft = new SmartDriveMotor(hwMap, "frontLeft", kF);
        frontRight = new SmartDriveMotor(hwMap, "frontRight", kF);
        backLeft = new SmartDriveMotor(hwMap, "backLeft", kF);
        backRight = new SmartDriveMotor(hwMap, "backRight", kF);

        frontRight.reverse();
        backRight.reverse();

        imu = hwMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(logoDir, usbDir)));
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
        imu.resetYaw();
    }

    public double getHeading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public double getYaw() {
        return getHeading();
    }

    public void setMecanumPowers(double fl, double fr, double bl, double br) {
        frontLeft.setDrivePower(fl);
        frontRight.setDrivePower(fr);
        backLeft.setDrivePower(bl);
        backRight.setDrivePower(br);
    }

    public void driveStraight(double power, double timeMs) {
        imu.resetYaw();

        ElapsedTime timer = new ElapsedTime();
        while (timer.milliseconds() < timeMs) {
            double yaw = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double correction = yaw * HEADING_GAIN;

            frontLeft.setDrivePower(power + correction);
            backLeft.setDrivePower(power + correction);
            frontRight.setDrivePower(power - correction);
            backRight.setDrivePower(power - correction);
        }
    }
}
