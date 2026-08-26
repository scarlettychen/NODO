package org.firstinspires.ftc.teamcode;

import com.nonodo.hardware.NODOChassis;
import com.nonodo.hardware.NODODrive;
import com.nonodo.hardware.NODODriveType;
import com.nonodo.hardware.NODOTankDrive;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Minimal auto: drive straight, then turn 90° (robot-relative).
 *
 * <p><b>Choose drive type</b> with {@link #DRIVE_TYPE} below (MECANUM or TANK).
 *
 * <p>This is a {@link LinearOpMode}: everything runs in {@link #runOpMode()}.
 * You do <b>not</b> need {@code start()} / {@code loop()} unless you use
 * {@code NODORoutine} in an iterative {@code OpMode}.
 */
@Autonomous(name = "Sample Drive + Turn 90", group = "NODO Samples")
public class SampleDriveAndTurn90 extends LinearOpMode {

    // ===== pick one =====
    private static final NODODriveType DRIVE_TYPE = NODODriveType.MECANUM;
    // private static final NODODriveType DRIVE_TYPE = NODODriveType.TANK;

    @Override
    public void runOpMode() {
        if (DRIVE_TYPE == NODODriveType.TANK) {
            NODOTankDrive.setMotorNames("leftDrive", "rightDrive");
        } else {
            NODOChassis.setMotorNames("frontLeft", "frontRight", "backLeft", "backRight");
        }

        NODODrive drive = new NODODrive(hardwareMap, DRIVE_TYPE, 0.03);

        drive.setControlHubOrientation(
                LogoFacingDirection.UP, UsbFacingDirection.FORWARD
        );

        // Optional: tune turn feel (defaults are 0.035 / 0.002 / max 0.35)
        // drive.setTurnPD(0.04, 0.0025);
        // drive.setTurnPD(0.04, 0.0025, 0.4); // kP, kD, maxPower

        if (drive.isMecanum()) {
            drive.setMecanumMotorDirections(
                    DcMotor.Direction.FORWARD,
                    DcMotor.Direction.REVERSE,
                    DcMotor.Direction.FORWARD,
                    DcMotor.Direction.REVERSE
            );
        } else {
            drive.setTankMotorDirections(
                    DcMotor.Direction.FORWARD,
                    DcMotor.Direction.REVERSE
            );
        }

        telemetry.addData("drive", DRIVE_TYPE);
        telemetry.addLine("Drive + Turn 90 ready. Press PLAY.");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) {
            return;
        }

        // LinearOpMode: call helpers in order — no start()/loop() needed.
        drive.driveFor(0.5, 800, this::opModeIsActive);
        drive.turnBy(90, this::opModeIsActive); // +90 = CCW; use -90 for CW

        telemetry.addData("heading", drive.getHeading());
        telemetry.addLine("done");
        telemetry.update();
    }
}
