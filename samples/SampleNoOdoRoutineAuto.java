package org.firstinspires.ftc.teamcode;

import com.nonodo.command.NODORoutine;
import com.nonodo.hardware.NODOChassis;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.LogoFacingDirection;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot.UsbFacingDirection;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Sample mecanum auto using {@link NODORoutine} only — no {@code NODOCommand} imports.
 * Queue steps in init, start on PLAY, tick in loop. turnTo() is robot-relative.
 *
 * <p>Prefer {@link SampleNoOdoLinearAuto} if you want chassis helpers with no routine.
 */
@Autonomous(name = "Sample No-Odo Routine Auto", group = "NODO Samples")
public class SampleNoOdoRoutineAuto extends OpMode {

    private NODOChassis chassis;
    private NODORoutine routine;

    @Override
    public void init() {
        NODOChassis.setMotorNames("frontLeft", "frontRight", "backLeft", "backRight");
        chassis = new NODOChassis(hardwareMap, 0.03);

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

        // Fluent steps only — no command classes in this OpMode.
        routine = new NODORoutine(chassis)
                .drive(0.5, 800)
                .waitMs(300)
                .strafe(0.4, 600)
                .waitMs(300)
                .turnTo(-90)
                .waitMs(150)
                .drive(0.5, 400);

        telemetry.addLine("Routine Auto ready. Press PLAY.");
        telemetry.update();
    }

    @Override
    public void start() {
        routine.start();
    }

    @Override
    public void loop() {
        routine.loop();
        telemetry.addData("heading", chassis.getHeading());
        telemetry.addData("done", routine.isFinished());
        telemetry.update();
    }

    @Override
    public void stop() {
        routine.cancelAll();
    }
}
