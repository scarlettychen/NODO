package com.nonodo.command;

public class SequentialGroup implements NoOdoCommand {

    private final NoOdoCommand[] commands;
    private int index;

    public SequentialGroup(NoOdoCommand... commands) {
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

        NoOdoCommand current = commands[index];
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
