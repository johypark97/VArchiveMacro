package com.github.johypark97.varchivemacro.macro.model;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

public class AnalysisData {
    public final Table<Enums.Button, Enums.Pattern, RecordData> recordDataTable =
            HashBasedTable.create();

    private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();
    private final ReadOnlyObjectWrapper<SongData> songData = new ReadOnlyObjectWrapper<>();

    public final SimpleObjectProperty<CaptureData> captureData = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Exception> exception = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Status> status = new SimpleObjectProperty<>(Status.READY);

    public AnalysisData(int id, SongData songData) {
        this.id.set(id);
        this.songData.set(songData);
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<SongData> songDataProperty() {
        return songData.getReadOnlyProperty();
    }

    public SimpleObjectProperty<CaptureData> captureDataProperty() {
        return captureData;
    }

    public SimpleObjectProperty<Exception> exceptionProperty() {
        return exception;
    }

    public SimpleObjectProperty<Status> statusProperty() {
        return status;
    }

    public void setException(Exception e) {
        exception.set(e);
        status.set(Status.ERROR);
    }

    public enum Status {
        ANALYZING,
        CANCELED,
        DONE,
        ERROR,
        LOADING,
        READY,
        WAITING
    }
}
