package com.github.johypark97.varchivemacro.macro.ocr;

import java.io.Serial;

public class PixError extends Exception {
    @Serial
    private static final long serialVersionUID = -7233163665094453999L;

    public PixError(String s) {
        super(s);
    }
}
