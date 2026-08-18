package com.nonodo.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NODOCommandScheduler {

    private final List<NODOCommand> activeCommands = new ArrayList<>();

    public void add(NODOCommand cmd) {
        cmd.init();
        activeCommands.add(cmd);
    }

    public void run() {
        Iterator<NODOCommand> iterator = activeCommands.iterator();
        while (iterator.hasNext()) {
            NODOCommand cmd = iterator.next();
            cmd.execute();
            if (cmd.isFinished()) {
                cmd.end();
                iterator.remove();
            }
        }
    }
}
