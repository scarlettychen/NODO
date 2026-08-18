package com.nonodo.command;

import com.qualcomm.robotcore.util.ElapsedTime;

public class WaitCommand implements NoOdoCommand {

    private final long timeMs;
    private ElapsedTime timer;

    public WaitCommand(long timeMs) {
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
