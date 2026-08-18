package com.nonodo.command;

public class NODOParallelGroup implements NODOCommand {

    private final NODOCommand[] commands;

    public NODOParallelGroup(NODOCommand... commands) {
        this.commands = commands;
    }

    @Override
    public void init() {
        for (NODOCommand command : commands) {
            command.init();
        }
    }

    @Override
    public void execute() {
        for (NODOCommand command : commands) {
            if (!command.isFinished()) {
                command.execute();
            }
        }
    }

    @Override
    public boolean isFinished() {
        for (NODOCommand command : commands) {
            if (!command.isFinished()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void end() {
        for (NODOCommand command : commands) {
            command.end();
        }
    }
}
