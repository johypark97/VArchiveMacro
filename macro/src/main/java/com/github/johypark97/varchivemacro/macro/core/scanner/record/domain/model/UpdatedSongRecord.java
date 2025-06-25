package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model;

public record UpdatedSongRecord(int songId, RecordButton button, RecordPattern pattern,
                                SongRecord newRecord) {
}
