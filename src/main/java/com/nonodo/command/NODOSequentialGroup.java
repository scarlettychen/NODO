package com.nonodo.command;

import java.util.List;

/**
 * Runs child {@link NODOCommand}s one after another as a single command.
 *
 * <p>Designed for the FTC OpMode loop: each {@link #execute()} advances at most
 * one child tick and safely hands off to the next when the current finishes.
 */
public class NODOSequentialGroup implements NODOCommand {

    private final NODOCommand[] commands;
    private int index;

    public NODOSequentialGroup(NODOCommand... commands) {
        this.commands = commands != null ? commands : new NODOCommand[0];
    }

    public NODOSequentialGroup(List<NODOCommand> commands) {
        this(commands != null
                ? commands.toArray(new NODOCommand[0])
                : new NODOCommand[0]);
    }

    @Override
    public void init() {
        index = 0;
        if (commands.length > 0) {
            commands[0].init();
        }
    }

    @Override
    public void execute() {
        if (isFinished()) {
            return;
        }

        NODOCommand current = commands[index];
        current.execute();

        if (current.isFinished()) {
            current.end();
            index++;
            if (!isFinished()) {
                commands[index].init();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return index >= commands.length;
    }

    /**
     * If the group is interrupted mid-sequence, end the active child so motors stop.
     */
    @Override
    public void end() {
        if (!isFinished()) {
            commands[index].end();
        }
    }

    public NODOCommand getCurrentCommand() {
        if (isFinished()) {
            return null;
        }
        return commands[index];
    }

    public int getIndex() {
        return index;
    }
}
