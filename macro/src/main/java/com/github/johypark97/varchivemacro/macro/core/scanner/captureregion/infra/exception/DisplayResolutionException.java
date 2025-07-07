package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception;

import java.io.Serial;

public class DisplayResolutionException extends Exception {
    @Serial
    private static final long serialVersionUID = 1587237792392870885L;

    public DisplayResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
