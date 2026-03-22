package com.github.johypark97.varchivemacro.libjfxhook.domain.exception;

import java.io.Serial;

public class JfxHookException extends Exception {
    @Serial
    private static final long serialVersionUID = 5596661593641173311L;

    public JfxHookException(Throwable cause) {
        super(cause);
    }
}
