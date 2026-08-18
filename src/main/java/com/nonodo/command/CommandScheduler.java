package com.nonodo.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandScheduler {

    private final List<NoOdoCommand> activeCommands = new ArrayList<>();

    public void add(NoOdoCommand cmd) {
        cmd.init();
        activeCommands.add(cmd);
    }

    public void run() {
        Iterator<NoOdoCommand> iterator = activeCommands.iterator();
        while (iterator.hasNext()) {
            NoOdoCommand cmd = iterator.next();
            cmd.execute();
            if (cmd.isFinished()) {
                cmd.end();
                iterator.remove();
            }
        }
    }
}
