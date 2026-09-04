package com.nonodo.blocks;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.ExportClassToBlocks;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;

/**
 * NODO movement blocks — use in Run after Start, stacked once (not inside repeat-while).
 */
@ExportClassToBlocks
public class NodoBlocksMotion extends BlocksOpModeCompanion {

    private NodoBlocksMotion() {
    }

    @ExportToBlocks(
            heading = "NODO Run",
            comment = "Drive straight: power (-1 to 1), duration in milliseconds. "
                    + "Same as Java driveFor(0.5, 800). NOT inside repeat-while.",
            tooltip = "Drive straight (power, ms)",
            parameterLabels = {"Power", "Duration (milliseconds)"},
            parameterDefaultValues = {"0.5", "800"}
    )
    public static void driveFor(double power, double durationMs) {
        NodoBlocksRuntime.driveFor(power, durationMs);
    }

    @ExportToBlocks(
            heading = "NODO Run",
            comment = "Strafe sideways: power (-1 to 1), duration in milliseconds. Mecanum only.",
            tooltip = "Strafe (power, ms)",
            parameterLabels = {"Power", "Duration (milliseconds)"},
            parameterDefaultValues = {"0.5", "600"}
    )
    public static void strafeFor(double power, double durationMs) {
        NodoBlocksRuntime.strafeFor(power, durationMs);
    }

    @ExportToBlocks(
            heading = "NODO Run",
            comment = "Turn by relative degrees (+ CCW, - CW). Gyro turn — not timed.",
            tooltip = "Turn relative (degrees)",
            parameterLabels = {"Turn Degrees (relative)"},
            parameterDefaultValues = {"90"}
    )
    public static void turnBy(double turnDegrees) {
        NodoBlocksRuntime.turnBy(turnDegrees);
    }

    @ExportToBlocks(
            heading = "NODO Run",
            comment = "Pause for durationMs with motors stopped.",
            tooltip = "Wait (milliseconds)",
            parameterLabels = {"Duration (milliseconds)"},
            parameterDefaultValues = {"500"}
    )
    public static void waitFor(double durationMs) {
        NodoBlocksRuntime.waitFor(durationMs);
    }

    @ExportToBlocks(
            heading = "NODO Run",
            comment = "Returns current gyro heading in degrees for telemetry.",
            tooltip = "Read heading (degrees)"
    )
    public static double getHeading() {
        return NodoBlocksRuntime.getHeading();
    }

    @ExportToBlocks(
            heading = "NODO Run",
            comment = "Stops all drive motors immediately.",
            tooltip = "Stop drive motors"
    )
    public static void stopDriveMotors() {
        NodoBlocksRuntime.stopDriveMotors();
    }
}
