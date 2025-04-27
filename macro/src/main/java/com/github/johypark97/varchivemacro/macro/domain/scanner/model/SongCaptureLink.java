package com.github.johypark97.varchivemacro.macro.domain.scanner.model;

public record SongCaptureLink(Song song, CaptureEntry captureEntry, int distance, double accuracy) {
}
