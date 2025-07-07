package com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.stream.Stream;

public class Capture {
    private final Table<RecordButton, RecordPattern, SongRecord> songRecordTable =
            HashBasedTable.create();

    public final CaptureRegion region;
    public final Song.Pack.Category category;
    public final String scannedTitle;

    public Capture(Song.Pack.Category category, String scannedTitle, CaptureRegion region) {
        this.category = category;
        this.region = region;
        this.scannedTitle = scannedTitle;
    }

    public boolean isSongRecordEmpty() {
        return songRecordTable.isEmpty();
    }

    public void clearSongRecord() {
        songRecordTable.clear();
    }

    public SongRecord getSongRecord(RecordButton button, RecordPattern pattern) {
        return songRecordTable.get(button, pattern);
    }

    public void setSongRecord(RecordButton button, RecordPattern pattern, SongRecord value) {
        songRecordTable.put(button, pattern, value);
    }

    public Stream<Cell> songRecordStream() {
        return songRecordTable.cellSet().stream().map(Cell::fromTableCell);
    }

    public record Cell(RecordButton button, RecordPattern pattern, SongRecord songRecord) {
        public static Cell fromTableCell(Table.Cell<RecordButton, RecordPattern, SongRecord> cell) {
            return new Cell(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }
}
