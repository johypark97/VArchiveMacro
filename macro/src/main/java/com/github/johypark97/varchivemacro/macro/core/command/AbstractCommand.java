package com.github.johypark97.varchivemacro.macro.core.command;

public abstract class AbstractCommand implements Command {
    protected Command nextCommand;

    @Override
    public Command getNext() {
        return nextCommand;
    }

    @Override
    public Command setNext(Command command) {
        nextCommand = command;
        return nextCommand;
    }
}
