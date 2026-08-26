package com.nonodo.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Do not use this class for arms or intakes. This feedforward math is specifically designed to overcome foam tile friction.
 */
public class SmartDriveMotor {

    private static final double NOMINAL_VOLTAGE = 13.0;
    private static final double FEEDFORWARD_DEADBAND = 0.01;

    private final DcMotorEx motor;
    private final BatteryVoltageFilter batteryVoltageFilter;
    private double kF;

    public SmartDriveMotor(HardwareMap hardwareMap, String motorName, double kF) {
        this(hardwareMap.get(DcMotorEx.class, motorName), BatteryVoltageFilter.getInstance(hardwareMap), kF);
    }

    /**
     * Maps a drive motor if it exists in the hardware map. Returns null instead of crashing
     * when a rookie config is missing a name.
     */
    public static SmartDriveMotor tryCreate(HardwareMap hardwareMap, String motorName, double kF) {
        if (hardwareMap == null || motorName == null) {
            return null;
        }
        DcMotorEx mappedMotor = hardwareMap.tryGet(DcMotorEx.class, motorName);
        if (mappedMotor == null) {
            return null;
        }
        return new SmartDriveMotor(mappedMotor, BatteryVoltageFilter.getInstance(hardwareMap), kF);
    }

    private SmartDriveMotor(DcMotorEx motor, BatteryVoltageFilter batteryVoltageFilter, double kF) {
        this.motor = motor;
        this.batteryVoltageFilter = batteryVoltageFilter;
        this.kF = kF;
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setKF(double newKF) {
        kF = newKF;
    }

    /** Alias for {@link #setKF(double)} — static tile-friction feedforward. */
    public void setFeedforward(double newKF) {
        setKF(newKF);
    }

    public void reverse() {
        setDirection(DcMotor.Direction.REVERSE);
    }

    public void setDirection(DcMotor.Direction direction) {
        motor.setDirection(direction);
    }

    public void setDrivePower(double targetPower) {
        setDrivePower(targetPower, true);
    }

    /**
     * @param applyFeedforward tile-friction {@code kF}. Turn-in-place should pass
     *                         {@code false}: near the setpoint P is smaller than {@code kF},
     *                         so feedforward flips sign every loop and the robot shakes.
     */
    public void setDrivePower(double targetPower, boolean applyFeedforward) {
        batteryVoltageFilter.update();
        double voltage = batteryVoltageFilter.getVoltage();
        // A 13V scale keeps joystick/auto power feeling the same as the battery sags.
        // Floor the divisor so a USB-only hub (near 0V) cannot explode the command.
        if (voltage < 1.0) {
            voltage = NOMINAL_VOLTAGE;
        }
        double adjustedPower = targetPower * (NOMINAL_VOLTAGE / voltage);

        if (applyFeedforward) {
            if (adjustedPower > FEEDFORWARD_DEADBAND) {
                adjustedPower += kF;
            } else if (adjustedPower < -FEEDFORWARD_DEADBAND) {
                adjustedPower -= kF;
            }
        }

        motor.setPower(adjustedPower);
    }
}
