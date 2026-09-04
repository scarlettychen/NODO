package org.firstinspires.ftc.teamcode;

import com.nonodo.hardware.NODOChassis;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Sample mecanum TeleOp using NODOChassis. Copy into TeamCode and edit hardware config.
 * Left stick translate, right stick X rotate. Y resets yaw.
 */
@TeleOp(name = "Sample No-Odo TeleOp", group = "NODO Samples")
public class SampleNoOdoTeleOp extends OpMode {

    private NODOChassis chassis;

    @Override
    public void init() {
        NODOChassis.setMotorNames("frontLeft", "frontRight", "backLeft", "backRight");
        chassis = new NODOChassis(hardwareMap, 0.03);
        chassis.setControlHubOrientation(
                LogoFacingDirection.UP, UsbFacingDirection.FORWARD
        );
        // Optional if you have an Expansion Hub IMU:
        // chassis.setExpansionHubOrientation(LogoFacingDirection.UP, UsbFacingDirection.FORWARD);
        chassis.applyMotorDirections(
                DcMotor.Direction.FORWARD,
                DcMotor.Direction.REVERSE,
                DcMotor.Direction.FORWARD,
                DcMotor.Direction.REVERSE
        );
        telemetry.addLine("Sample TeleOp ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        if (gamepad1.y) {
            chassis.resetYaw();
        }

        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double fl = drive + strafe + turn;
        double fr = drive - strafe - turn;
        double bl = drive - strafe + turn;
        double br = drive + strafe - turn;

        double max = Math.max(Math.abs(fl), Math.abs(fr));
        max = Math.max(max, Math.abs(bl));
        max = Math.max(max, Math.abs(br));
        if (max > 1.0) {
            fl /= max;
            fr /= max;
            bl /= max;
            br /= max;
        }

        chassis.setMecanumPowers(fl, fr, bl, br);
        telemetry.addData("heading", chassis.getHeading());
        telemetry.addLine("Y = reset yaw");
        telemetry.update();
    }

    @Override
    public void stop() {
        chassis.setMecanumPowers(0, 0, 0, 0);
    }
}
