package com.github.johypark97.varchivemacro.macro.core.scanner.ocr.infra.exception;

import java.io.Serial;

public class OcrInitializationException extends Exception {
    @Serial
    private static final long serialVersionUID = -4702898127862988176L;

    public OcrInitializationException(Throwable cause) {
        super(cause);
    }
}
