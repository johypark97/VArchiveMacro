package com.github.johypark97.varchivemacro.macro.core.exception;

import java.io.Serial;

public class RecordNotLoadedException extends Exception {
    @Serial
    private static final long serialVersionUID = -3210770118374873868L;

    public RecordNotLoadedException() {
        super("Record not loaded");
    }
}
