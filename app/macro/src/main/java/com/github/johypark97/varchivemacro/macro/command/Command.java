package com.github.johypark97.varchivemacro.macro.command;

public interface Command {
    Command getNext();

    Command setNext(Command command);

    boolean run();
}
