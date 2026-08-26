package com.nonodo.blocks;

import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion;
import org.firstinspires.ftc.robotcore.external.ExportClassToBlocks;
import org.firstinspires.ftc.robotcore.external.ExportToBlocks;

/**
 * Drive-type constants for Blocks. Use with {@link NodoBlocksFramework#initializeDrive(int)}:
 *
 * <pre>
 * initializeDrive( DRIVE_TYPE.MECANUM )
 * initializeDrive( DRIVE_TYPE.TANK )
 * </pre>
 *
 * <p>Same values as Java {@link com.nonodo.hardware.NODODriveType} (mecanum = 0, tank = 1).
 */
@ExportClassToBlocks
public class DRIVE_TYPE extends BlocksOpModeCompanion {

    private DRIVE_TYPE() {
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Mecanum drive type constant (0). Plug into initializeDrive, or use initializeMecanumDrive instead.",
            tooltip = "DRIVE_TYPE.MECANUM"
    )
    public static int MECANUM() {
        return NodoBlocksFramework.DRIVE_TYPE_MECANUM;
    }

    @ExportToBlocks(
            heading = "NODO",
            comment = "Tank drive type constant (1). Plug into initializeDrive, or use initializeTankDrive instead.",
            tooltip = "DRIVE_TYPE.TANK"
    )
    public static int TANK() {
        return NodoBlocksFramework.DRIVE_TYPE_TANK;
    }
}
