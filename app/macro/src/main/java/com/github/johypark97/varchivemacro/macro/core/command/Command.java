package com.github.johypark97.varchivemacro.macro.core.command;

public interface Command {
    Command getNext();

    Command setNext(Command command);

    boolean run();
}
