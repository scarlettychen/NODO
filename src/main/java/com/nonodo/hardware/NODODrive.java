package com.nonodo.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.function.BooleanSupplier;

/**
 * Single entry point that wraps either {@link NODOChassis} (mecanum) or
 * {@link NODOTankDrive} (tank). Choose with {@link NODODriveType} at construct time.
 *
 * <pre>{@code
 * NODODrive drive = new NODODrive(hardwareMap, NODODriveType.MECANUM, 0.03);
 * // or NODODriveType.TANK
 * drive.setControlHubOrientation(LogoFacingDirection.UP, UsbFacingDirection.FORWARD);
 * drive.driveFor(0.5, 800, this::opModeIsActive);
 * drive.turnBy(90, this::opModeIsActive);
 * }</pre>
 */
public class NODODrive {

    private final NODODriveType type;
    private final NODOChassis mecanum;
    private final NODOTankDrive tank;

    public NODODrive(HardwareMap hardwareMap, NODODriveType type, double kF) {
        if (type == null) {
            throw new IllegalArgumentException("type must be MECANUM or TANK");
        }
        this.type = type;
        if (type == NODODriveType.TANK) {
            this.tank = new NODOTankDrive(hardwareMap, kF);
            this.mecanum = null;
        } else {
            this.mecanum = new NODOChassis(hardwareMap, kF);
            this.tank = null;
        }
    }

    public NODODriveType getType() {
        return type;
    }

    public boolean isMecanum() {
        return type == NODODriveType.MECANUM;
    }

    public boolean isTank() {
        return type == NODODriveType.TANK;
    }

    /** Underlying mecanum chassis, or {@code null} if tank. */
    public NODOChassis getMecanum() {
        return mecanum;
    }

    /** Underlying tank drive, or {@code null} if mecanum. */
    public NODOTankDrive getTank() {
        return tank;
    }

    public void setControlHubOrientation(LogoFacingDirection logo, UsbFacingDirection usb) {
        if (mecanum != null) {
            mecanum.setControlHubOrientation(logo, usb);
        } else {
            tank.setControlHubOrientation(logo, usb);
        }
    }

    /** Mecanum only; no-op on tank. */
    public void setExpansionHubOrientation(LogoFacingDirection logo, UsbFacingDirection usb) {
        if (mecanum != null) {
            mecanum.setExpansionHubOrientation(logo, usb);
        }
    }

    public void setMecanumMotorDirections(
            DcMotor.Direction frontLeft,
            DcMotor.Direction frontRight,
            DcMotor.Direction backLeft,
            DcMotor.Direction backRight
    ) {
        if (mecanum != null) {
            mecanum.setMotorDirections(frontLeft, frontRight, backLeft, backRight);
        }
    }

    public void setTankMotorDirections(DcMotor.Direction left, DcMotor.Direction right) {
        if (tank != null) {
            tank.setMotorDirections(left, right);
        }
    }

    public TurnPDGains getTurnPD() {
        return mecanum != null ? mecanum.getTurnPD() : tank.getTurnPD();
    }

    /** Sets turn PD gains (defaults 0.035 / 0.002). */
    public void setTurnPD(double kP, double kD) {
        getTurnPD().setPD(kP, kD);
    }

    /** Sets turn PD gains and max power clamp (default max 0.8). */
    public void setTurnPD(double kP, double kD, double maxPower) {
        getTurnPD().setPD(kP, kD).setMaxPower(maxPower);
    }

    public void setTurnToleranceDegrees(double toleranceDegrees) {
        getTurnPD().setToleranceDegrees(toleranceDegrees);
    }

    public void setTurnSettleMs(double settleMs) {
        getTurnPD().setSettleMs(settleMs);
    }

    /** Sets static tile-friction feedforward ({@code kF}) on all drive motors. Typical start: {@code 0.03}. */
    public void setFeedforward(double kF) {
        if (mecanum != null) {
            mecanum.setFeedforward(kF);
        } else {
            tank.setFeedforward(kF);
        }
    }

    public void resetYaw() {
        if (mecanum != null) {
            mecanum.resetYaw();
        } else {
            tank.resetYaw();
        }
    }

    public double getHeading() {
        return mecanum != null ? mecanum.getHeading() : tank.getHeading();
    }

    public double getRawHeading() {
        return mecanum != null ? mecanum.getRawHeading() : tank.getRawHeading();
    }

    public void stop() {
        if (mecanum != null) {
            mecanum.stop();
        } else {
            tank.stop();
        }
    }

    public void driveFor(double power, long timeMs, BooleanSupplier isActive) {
        if (mecanum != null) {
            mecanum.driveFor(power, timeMs, isActive);
        } else {
            tank.driveFor(power, timeMs, isActive);
        }
    }

    /** Mecanum only; no-op on tank. */
    public void strafeFor(double power, long timeMs, BooleanSupplier isActive) {
        if (mecanum != null) {
            mecanum.strafeFor(power, timeMs, isActive);
        }
    }

    public void turnBy(double relativeDegrees, BooleanSupplier isActive) {
        if (mecanum != null) {
            mecanum.turnBy(relativeDegrees, isActive);
        } else {
            tank.turnBy(relativeDegrees, isActive);
        }
    }

    public void waitFor(long timeMs, BooleanSupplier isActive) {
        if (mecanum != null) {
            mecanum.waitFor(timeMs, isActive);
        } else {
            tank.waitFor(timeMs, isActive);
        }
    }
}
