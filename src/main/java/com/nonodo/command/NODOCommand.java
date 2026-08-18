package com.nonodo.command;

public interface NODOCommand {
    void init();
    void execute();
    boolean isFinished();
    void end();
}
