package com.github.johypark97.varchivemacro.lib.scanner.area;

import java.awt.Dimension;
import java.io.Serial;

public class NotSupportedResolutionException extends Exception {
    @Serial
    private static final long serialVersionUID = -4175436171492621641L;

    private static final String MESSAGE_FORMAT = "not supported resolution: %dx%d";

    public NotSupportedResolutionException(int width, int height) {
        super(String.format(MESSAGE_FORMAT, width, height));
    }

    public NotSupportedResolutionException(Dimension resolution) {
        this(resolution.width, resolution.height);
    }
}
