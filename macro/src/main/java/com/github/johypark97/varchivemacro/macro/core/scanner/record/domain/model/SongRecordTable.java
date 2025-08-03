package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.Optional;
import java.util.stream.Stream;

public class SongRecordTable {
    private final Table<RecordButton, RecordPattern, SongRecord> recordTable =
            HashBasedTable.create();

    public final int songId;

    public SongRecordTable(int songId) {
        this.songId = songId;
    }

    public SongRecord getSongRecord(RecordButton button, RecordPattern pattern) {
        return Optional.ofNullable(recordTable.get(button, pattern))
                .orElse(new SongRecord(0, false));
    }

    public void setSongRecord(RecordButton button, RecordPattern pattern, SongRecord value) {
        recordTable.put(button, pattern, value);
    }

    public Stream<Cell> recordStream() {
        return recordTable.cellSet().stream().map(Cell::fromTableCell);
    }

    public record Cell(RecordButton button, RecordPattern pattern, SongRecord record) {
        public static Cell fromTableCell(Table.Cell<RecordButton, RecordPattern, SongRecord> cell) {
            return new Cell(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }
}
