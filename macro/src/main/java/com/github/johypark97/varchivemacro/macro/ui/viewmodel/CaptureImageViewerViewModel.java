package com.github.johypark97.varchivemacro.macro.ui.viewmodel;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;

public class CaptureImageViewerViewModel {
    public record CaptureImage(int entryId, String scannedTitle) {
        public static CaptureImage from(CaptureEntry captureEntry) {
            return new CaptureImage(captureEntry.entryId(), captureEntry.capture().scannedTitle);
        }
    }
}
