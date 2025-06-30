package com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception;

import java.io.Serial;

public class PixImageException extends Exception {
    @Serial
    private static final long serialVersionUID = -7848897078269346989L;

    public PixImageException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
