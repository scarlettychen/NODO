package com.nonodo.command;

import com.qualcomm.robotcore.util.ElapsedTime;

public class NODOWaitCommand implements NODOCommand {

    private final long timeMs;
    private ElapsedTime timer;

    public NODOWaitCommand(long timeMs) {
        this.timeMs = timeMs;
    }

    @Override
    public void init() {
        timer = new ElapsedTime();
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeMs;
    }

    @Override
    public void end() {
    }
}
