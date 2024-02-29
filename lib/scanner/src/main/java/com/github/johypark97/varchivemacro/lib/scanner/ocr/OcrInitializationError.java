package com.github.johypark97.varchivemacro.lib.scanner.ocr;

import java.io.Serial;

public class OcrInitializationError extends Exception {
    @Serial
    private static final long serialVersionUID = -6841398364086744706L;

    public OcrInitializationError(String message) {
        super(message);
    }
}
