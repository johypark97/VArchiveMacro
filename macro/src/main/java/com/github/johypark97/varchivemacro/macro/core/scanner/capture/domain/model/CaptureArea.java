package com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;

public record CaptureArea(SongRecord record, CaptureBound rateBound, CaptureBound maxComboBound) {
}
