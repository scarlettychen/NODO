package org.firstinspires.ftc.teamcode;

import com.nonodo.hardware.NODOChassis;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Sample mecanum auto using only {@link NODOChassis} — no commands / routines.
 * Copy into TeamCode and edit hardware config.
 * turnBy() is robot-relative (degrees from current heading).
 */
@Autonomous(name = "Sample No-Odo Linear Auto", group = "NODO Samples")
public class SampleNoOdoLinearAuto extends LinearOpMode {

    @Override
    public void runOpMode() {
        NODOChassis.setMotorNames("frontLeft", "frontRight", "backLeft", "backRight");
        NODOChassis chassis = new NODOChassis(hardwareMap, 0.03);

        chassis.setControlHubOrientation(
                LogoFacingDirection.UP, UsbFacingDirection.FORWARD
        );
        // Optional if you have an Expansion Hub IMU:
        // chassis.setExpansionHubOrientation(LogoFacingDirection.UP, UsbFacingDirection.FORWARD);
        chassis.setMotorDirections(
                DcMotor.Direction.FORWARD,
                DcMotor.Direction.REVERSE,
                DcMotor.Direction.FORWARD,
                DcMotor.Direction.REVERSE
        );

        telemetry.addLine("Sample Linear Auto ready. Press PLAY.");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) {
            return;
        }

        chassis.driveFor(0.5, 800, this::opModeIsActive);
        chassis.waitFor(300, this::opModeIsActive);
        chassis.strafeFor(0.4, 600, this::opModeIsActive);
        chassis.waitFor(300, this::opModeIsActive);
        chassis.turnBy(-90, this::opModeIsActive);
        chassis.waitFor(150, this::opModeIsActive);
        chassis.driveFor(0.5, 400, this::opModeIsActive);

        telemetry.addData("heading", chassis.getHeading());
        telemetry.addLine("done");
        telemetry.update();
    }
}
