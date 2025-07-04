package com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.stream.Stream;

public class Capture {
    private final Table<RecordButton, RecordPattern, CaptureArea> captureAreaTable =
            HashBasedTable.create();

    public final CaptureBound scannedTitleBound;
    public final Song.Pack.Category category;
    public final String scannedTitle;

    public Capture(Song.Pack.Category category, String scannedTitle,
            CaptureBound scannedTitleBound) {
        this.category = category;
        this.scannedTitle = scannedTitle;
        this.scannedTitleBound = scannedTitleBound;
    }

    public boolean isCaptureAreaEmpty() {
        return captureAreaTable.isEmpty();
    }

    public void clearCaptureAreaEmpty() {
        captureAreaTable.clear();
    }

    public CaptureArea getCaptureArea(RecordButton button, RecordPattern pattern) {
        return captureAreaTable.get(button, pattern);
    }

    public void setCaptureArea(RecordButton button, RecordPattern pattern, CaptureArea value) {
        captureAreaTable.put(button, pattern, value);
    }

    public Stream<Cell> captureAreaStream() {
        return captureAreaTable.cellSet().stream().map(Cell::fromTableCell);
    }

    public record Cell(RecordButton button, RecordPattern pattern, CaptureArea area) {
        public static Cell fromTableCell(
                Table.Cell<RecordButton, RecordPattern, CaptureArea> cell) {
            return new Cell(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
        }
    }
}
