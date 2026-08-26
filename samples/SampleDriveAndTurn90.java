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
 * Testing auton. Drives straight, then turns 90 degrees
 */
@Autonomous(name = "Sample Drive + Turn 90")
public class SampleDriveAndTurn90 extends LinearOpMode {

    // ===== pick one of these =====
    private static final NODODriveType DRIVE_TYPE = NODODriveType.MECANUM;
    // private static final NODODriveType DRIVE_TYPE = NODODriveType.TANK;

    @Override
    public void runOpMode() {
        // ==== pick one =====
        setMecanum("FL", "FR", "BL", "BR", 
            DcMotor.Direction.FORWARD, 
            DcMotor.Direction.REVERSE, 
            DcMotor.Direction.FORWARD, 
            DcMotor.Direction.REVERSE);

        // setTank("leftDrive", "rightDrive", 
        //     DcMotor.Direction.FORWARD, 
        //     DcMotor.Direction.REVERSE);

        NODODrive drive = new NODODrive(hardwareMap, DRIVE_TYPE, 0.03);

        drive.setControlHubOrientation(
                LogoFacingDirection.UP, UsbFacingDirection.FORWARD
        );

        drive.setTurnPD(0.04, 0.0025);

        telemetry.addData("drive", DRIVE_TYPE);
        telemetry.addLine("Ready.");
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

   public void setMecanum(String fl, String fr, String bl, String br,
                            DcMotor.Direction flDir, DcMotor.Direction frDir, DcMotor.Direction blDir, DcMotor.Direction brDir
    ) {
        NODOChassis.setMotorNames(fl, fr, bl, br);
        NODOChassis.setMotorDirections(flDir, frDir, blDir, brDir);
    }

    public void setTank(String l, String r, DcMotor.Direction lDir, DcMotor.Direction rDir
    ) {
        NODOTankDrive.setMotorNames(l, r);
        NODOTankDrive.setMotorDirections(lDir, rDir);
    }
    
}