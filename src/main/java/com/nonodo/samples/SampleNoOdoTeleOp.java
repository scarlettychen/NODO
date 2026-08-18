package com.nonodo.samples;

import com.nonodo.hardware.NoOdoChassis;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Sample No-Odo TeleOp")
public class SampleNoOdoTeleOp extends OpMode {

    private NoOdoChassis chassis;
    private DcMotorEx armMotor;

    @Override
    public void init() {
        chassis = new NoOdoChassis(hardwareMap, 0.05);
        armMotor = hardwareMap.get(DcMotorEx.class, "armMotor");
    }

    @Override
    public void loop() {
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double frontLeftPower = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower = drive - strafe + turn;
        double backRightPower = drive + strafe - turn;

        double max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));
        if (max > 1.0) {
            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        // Wheels use SmartDriveMotor because they roll on foam tiles.
        // setDrivePower() boosts command as the battery sags (voltage compensation)
        // and adds a small static feedforward so the robot can overcome tile friction
        // instead of sitting still at low joystick values.
        chassis.getFrontLeft().setDrivePower(frontLeftPower);
        chassis.getFrontRight().setDrivePower(frontRightPower);
        chassis.getBackLeft().setDrivePower(backLeftPower);
        chassis.getBackRight().setDrivePower(backRightPower);

        // The arm is NOT a drive wheel. SmartDriveMotor's +0.05 friction kick and
        // 13V battery scaling are meant for foam-tile rolling resistance, not gravity.
        // Using that class here would make the arm lurch or drift. A plain DcMotorEx
        // gets the raw trigger value with no drive-specific math.
        double armPower = gamepad1.right_trigger - gamepad1.left_trigger;
        armMotor.setPower(armPower);
    }
}
