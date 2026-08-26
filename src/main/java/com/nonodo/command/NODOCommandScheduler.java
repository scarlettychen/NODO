package com.nonodo.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Runs active {@link NODOCommand}s each OpMode loop cycle.
 *
 * <p>Instantiate once per OpMode (typically owned by {@link NODORoutine}). Call
 * {@link #run()} from {@code loop()} and {@link #cancelAll()} from {@code stop()}.
 *
 * <p>{@link #run()} uses an {@link Iterator} so finished commands are removed
 * without a {@link java.util.ConcurrentModificationException}.
 */
public class NODOCommandScheduler {

    private final List<NODOCommand> activeCommands = new ArrayList<>();

    /**
     * Initializes {@code command} immediately and adds it to the active set.
     */
    public void add(NODOCommand command) {
        if (command == null) {
            return;
        }
        command.init();
        activeCommands.add(command);
    }

    /**
     * Tick every active command once. Safe to call every FTC {@code loop()}.
     * Finished commands get {@link NODOCommand#end()} then are removed.
     */
    public void run() {
        Iterator<NODOCommand> iterator = activeCommands.iterator();
        while (iterator.hasNext()) {
            NODOCommand command = iterator.next();
            command.execute();
            if (command.isFinished()) {
                command.end();
                iterator.remove();
            }
        }
    }

    /**
     * Ends and clears every active command. Call from OpMode {@code stop()}.
     */
    public void cancelAll() {
        for (NODOCommand command : activeCommands) {
            try {
                command.end();
            } catch (Exception ignored) {
                // Never let cleanup crash the OpMode.
            }
        }
        activeCommands.clear();
    }

    public boolean isIdle() {
        return activeCommands.isEmpty();
    }

    /**
     * First active root command (often a {@link NODOSequentialGroup}).
     */
    public NODOCommand getActiveCommand() {
        return activeCommands.isEmpty() ? null : activeCommands.get(0);
    }
}
