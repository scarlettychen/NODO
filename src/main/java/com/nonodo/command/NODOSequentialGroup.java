package com.nonodo.command;

public class NODOSequentialGroup implements NODOCommand {

    private final NODOCommand[] commands;
    private int index;

    public NODOSequentialGroup(NODOCommand... commands) {
        this.commands = commands;
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

    @Override
    public void end() {
        if (!isFinished()) {
            commands[index].end();
        }
    }
}
