package com.github.johypark97.varchivemacro.macro.integration.app.scanner.review;

import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;

public record LinkedCaptureData(CaptureData captureData, int distance) {
    public static LinkedCaptureData from(SongCaptureLink songCaptureLink) {
        return new LinkedCaptureData(CaptureData.from(songCaptureLink.captureEntry()),
                songCaptureLink.distance());
    }
}
