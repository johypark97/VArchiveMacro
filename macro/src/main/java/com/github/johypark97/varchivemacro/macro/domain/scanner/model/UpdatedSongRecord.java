package com.github.johypark97.varchivemacro.macro.domain.scanner.model;

public record UpdatedSongRecord(int songId, RecordButton button, RecordPattern pattern,
                                SongRecord newRecord) {
}
