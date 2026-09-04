package com.nonodo.blocks;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.ExportClassToBlocks;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;

/**
 * NODO setup blocks — use in the Init section (before Start).
 */
@ExportClassToBlocks
public class NodoBlocksInit extends BlocksOpModeCompanion {

    private NodoBlocksInit() {
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Optional. Sets feedforward kF before init (default 0.03). Same as Java NODODrive(..., kF).",
            tooltip = "Set feedforward kF",
            parameterLabels = {"Feedforward (kF)"},
            parameterDefaultValues = {"0.03"}
    )
    public static void setFeedforward(double kF) {
        NodoBlocksRuntime.setFeedforward(kF);
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Sets mecanum motor names and directions before initializeMecanumDrive. "
                    + "Directions: type FORWARD or REVERSE.",
            tooltip = "Set mecanum names + directions",
            parameterLabels = {
                    "Front Left Name", "Front Right Name", "Back Left Name", "Back Right Name",
                    "Front Left Dir", "Front Right Dir", "Back Left Dir", "Back Right Dir"
            },
            parameterDefaultValues = {
                    "FL", "FR", "BL", "BR",
                    "FORWARD", "REVERSE", "FORWARD", "REVERSE"
            }
    )
    public static void setMecanum(
            String frontLeftName,
            String frontRightName,
            String backLeftName,
            String backRightName,
            String frontLeftDirection,
            String frontRightDirection,
            String backLeftDirection,
            String backRightDirection
    ) {
        NodoBlocksRuntime.setMecanum(
                frontLeftName, frontRightName, backLeftName, backRightName,
                frontLeftDirection, frontRightDirection, backLeftDirection, backRightDirection
        );
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Sets tank motor names and directions before initializeTankDrive. "
                    + "Directions: type FORWARD or REVERSE.",
            tooltip = "Set tank names + directions",
            parameterLabels = {"Left Name", "Right Name", "Left Dir", "Right Dir"},
            parameterDefaultValues = {"leftDrive", "rightDrive", "FORWARD", "REVERSE"}
    )
    public static void setTank(
            String leftName,
            String rightName,
            String leftDirection,
            String rightDirection
    ) {
        NodoBlocksRuntime.setTank(leftName, rightName, leftDirection, rightDirection);
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Initialize mecanum drive after setMecanum. Call once in Init.",
            tooltip = "Init mecanum drive"
    )
    public static void initializeMecanumDrive() {
        NodoBlocksRuntime.initializeMecanumDrive();
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Initialize tank drive after setTank. Call once in Init. Strafe will not work.",
            tooltip = "Init tank drive"
    )
    public static void initializeTankDrive() {
        NodoBlocksRuntime.initializeTankDrive();
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Required after init. Logo/USB: UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT.",
            tooltip = "Set Control Hub orientation",
            parameterLabels = {"Logo Facing", "USB Facing"},
            parameterDefaultValues = {"UP", "FORWARD"}
    )
    public static void setControlHubOrientation(String logoFacing, String usbFacing) {
        NodoBlocksRuntime.setControlHubOrientation(logoFacing, usbFacing);
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Optional mecanum-only Expansion Hub IMU (default name imu2).",
            tooltip = "Set Expansion Hub orientation",
            parameterLabels = {"Logo Facing", "USB Facing"},
            parameterDefaultValues = {"UP", "FORWARD"}
    )
    public static void setExpansionHubOrientation(String logoFacing, String usbFacing) {
        NodoBlocksRuntime.setExpansionHubOrientation(logoFacing, usbFacing);
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Optional turn kP and kD. Same as Java drive.setTurnPD(0.04, 0.0025).",
            tooltip = "Set turn kP + kD",
            parameterLabels = {"kP", "kD"},
            parameterDefaultValues = {"0.04", "0.0025"}
    )
    public static void setTurnPD(double kP, double kD) {
        NodoBlocksRuntime.setTurnPD(kP, kD);
    }

    @ExportToBlocks(
            heading = "NODO Init",
            comment = "Optional turn kP, kD, and max power (default max 0.8).",
            tooltip = "Set turn PD + max power",
            parameterLabels = {"kP", "kD", "Max Power"},
            parameterDefaultValues = {"0.035", "0.002", "0.8"}
    )
    public static void setTurnPD(double kP, double kD, double maxPower) {
        NodoBlocksRuntime.setTurnPD(kP, kD, maxPower);
    }
}
