package org.firstinspires.ftc.teamcode;

import com.nonodo.command.NODOTankRoutine;
import com.nonodo.hardware.NODOTankDrive;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Sample tank auto using NODO. Copy into TeamCode and edit hardware config.
 */
@Autonomous(name = "Sample No-Odo Tank Auto", group = "NODO Samples")
public class SampleNoOdoTankAuto extends OpMode {

    private NODOTankDrive drive;
    private NODOTankRoutine routine;

    @Override
    public void init() {
        NODOTankDrive.setMotorNames("leftDrive", "rightDrive");
        drive = new NODOTankDrive(hardwareMap, 0.03);
        drive.setControlHubOrientation(LogoFacingDirection.UP, UsbFacingDirection.FORWARD);
        drive.applyMotorDirections(DcMotor.Direction.FORWARD, DcMotor.Direction.REVERSE);

        routine = new NODOTankRoutine(drive)
                .drive(0.5, 800)
                .waitMs(300)
                .turnTo(-90)
                .waitMs(150)
                .drive(0.5, 400);

        telemetry.addLine("Sample Tank Auto ready. Press PLAY.");
        telemetry.update();
    }

    @Override
    public void start() {
        routine.start();
    }

    @Override
    public void loop() {
        routine.loop();
        telemetry.addData("heading", drive.getHeading());
        telemetry.addData("done", routine.isFinished());
        telemetry.update();
    }

    @Override
    public void stop() {
        routine.cancelAll();
    }
}
