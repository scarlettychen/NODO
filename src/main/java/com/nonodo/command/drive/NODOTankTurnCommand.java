package com.nonodo.command.drive;

import com.nonodo.command.NODOCommand;
import com.nonodo.hardware.NODOTankDrive;
import com.nonodo.hardware.TankRelativeTurnController;

/**
 * Tank robot-relative PD turn.
 * Optional — prefer {@link NODOTankDrive#turnBy} if you are not using commands.
 */
public class NODOTankTurnCommand implements NODOCommand {

    private final TankRelativeTurnController turn;
    private final double requestedDegrees;

    public NODOTankTurnCommand(NODOTankDrive drive, double turnDegrees) {
        this.turn = new TankRelativeTurnController(drive);
        this.requestedDegrees = turnDegrees;
    }

    @Override
    public void init() {
        turn.start(requestedDegrees);
    }

    @Override
    public void execute() {
        turn.update();
    }

    @Override
    public boolean isFinished() {
        return turn.isFinished();
    }

    @Override
    public void end() {
        turn.end();
    }

    public double getError() {
        return turn.getError();
    }

    public double getCommandedPower() {
        return turn.getCommandedPower();
    }
}
