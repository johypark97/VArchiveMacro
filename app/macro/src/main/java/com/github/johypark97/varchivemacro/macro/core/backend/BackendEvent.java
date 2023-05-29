package com.github.johypark97.varchivemacro.macro.core.backend;

import java.util.List;

public class BackendEvent {
    public final Exception exception;
    public final List<String> argList;
    public final Type type;

    public BackendEvent(Type type) {
        this(type, null);
    }

    public BackendEvent(Type type, List<String> args) {
        this.type = type;

        argList = args;
        exception = (type == Type.EXCEPTION) ? new RuntimeException(
                "The type of the BackendEvent is set to EXCEPTION explicitly") : null; // NOPMD
    }

    public BackendEvent(Exception exception) {
        this.exception = exception;

        argList = null;
        type = Type.EXCEPTION;
    }

    public enum Type {
        // @formatter:off
        CANCELED,
        CLIENT_MACRO_START,
        DONE,
        EXCEPTION,
        IS_NOT_RUNNING,
        IS_RUNNING,
        LOAD_REMOTE_RECORD,
        SCANNER_CAPTURE_DONE,
        SCANNER_START_ANALYZE,
        SCANNER_START_CAPTURE,
        SCANNER_START_COLLECT_RESULT,
        SCANNER_START_UPLOAD_RECORD,
        START_COMMAND,
        // @formatter:on
    }
}
