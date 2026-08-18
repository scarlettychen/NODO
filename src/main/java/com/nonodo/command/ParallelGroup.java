package com.nonodo.command;

public class ParallelGroup implements NoOdoCommand {

    private final NoOdoCommand[] commands;

    public ParallelGroup(NoOdoCommand... commands) {
        this.commands = commands;
    }

    @Override
    public void init() {
        for (NoOdoCommand command : commands) {
            command.init();
        }
    }

    @Override
    public void execute() {
        for (NoOdoCommand command : commands) {
            if (!command.isFinished()) {
                command.execute();
            }
        }
    }

    @Override
    public boolean isFinished() {
        for (NoOdoCommand command : commands) {
            if (!command.isFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void end() {
        for (NoOdoCommand command : commands) {
            command.end();
        }
    }
}
