package com.nonodo.command;

public interface NoOdoCommand {
    void init();
    void execute();
    boolean isFinished();
    void end();
}
