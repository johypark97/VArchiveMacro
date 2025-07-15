package com.github.johypark97.varchivemacro.macro.integration.app.scanner.review;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;

public record CaptureData(int entryId, String scannedTitle) {
    public static CaptureData from(CaptureEntry captureEntry) {
        return new CaptureData(captureEntry.entryId(), captureEntry.capture().scannedTitle);
    }
}
