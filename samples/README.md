# Sample OpModes

Copy these into your FTC project's `TeamCode` package (e.g. `org.firstinspires.ftc.teamcode`), then change motor names, hub orientation, and directions to match your robot.

These files are **not** compiled into the NODO AAR — they are examples only.

| File | Purpose |
|------|---------|
| `SampleDriveAndTurn90.java` | Pick `MECANUM`/`TANK`, drive + turn 90° — LinearOpMode (no `start`/`loop`) |
| `SampleNoOdoLinearAuto.java` | Chassis helpers only (`driveFor` / `strafeFor` / `turnBy`) — no routine, no commands |
| `SampleNoOdoRoutineAuto.java` | Iterative OpMode with `NODORoutine` fluent API — no `NODOCommand` imports |
| `SampleNoOdoTeleOp.java` | Mecanum teleop with yaw reset (chassis only) |
| `SampleNoOdoTankAuto.java` | Tank `NODOTankRoutine` |

After adding the Gradle dependency, sync and deploy from **your** Robot Controller project as usual.
