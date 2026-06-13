package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.exception;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.Resolution;
import java.io.Serial;

public class NotSupportedResolutionException extends Exception {
    @Serial
    private static final long serialVersionUID = -4175436171492621641L;

    public NotSupportedResolutionException(Resolution resolution) {
        super("Not supported resolution: " + resolution.getWidth() + "x" + resolution.getHeight());
    }
}
