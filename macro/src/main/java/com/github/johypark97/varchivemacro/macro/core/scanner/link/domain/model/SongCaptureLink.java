package com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;

public record SongCaptureLink(Song song, CaptureEntry captureEntry, int distance, double accuracy) {
}
