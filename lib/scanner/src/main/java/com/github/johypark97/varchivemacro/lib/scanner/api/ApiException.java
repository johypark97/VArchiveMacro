package com.github.johypark97.varchivemacro.lib.scanner.api;

import java.io.Serial;

public class ApiException extends Exception {
    @Serial
    private static final long serialVersionUID = 2479973042509948120L;

    public ApiException(String message) {
        super(message);
    }
}
