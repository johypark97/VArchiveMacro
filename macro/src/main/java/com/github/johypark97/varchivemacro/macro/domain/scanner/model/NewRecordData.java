package com.github.johypark97.varchivemacro.macro.domain.scanner.model;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class NewRecordData {
    private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();
    private final ReadOnlyObjectWrapper<SongDatabase.Song> song = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<RecordManager.LocalRecord> newRecord =
            new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<RecordManager.LocalRecord> previousRecord =
            new ReadOnlyObjectWrapper<>();

    public final SimpleBooleanProperty selected = new SimpleBooleanProperty();
    public final SimpleObjectProperty<Status> status = new SimpleObjectProperty<>(Status.NONE);

    public NewRecordData(int id, SongDatabase.Song song, RecordManager.LocalRecord previousRecord,
            RecordManager.LocalRecord newRecord) {
        this.id.set(id);
        this.newRecord.set(newRecord);
        this.previousRecord.set(previousRecord.clone());
        this.song.set(song);
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<SongDatabase.Song> songProperty() {
        return song.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<RecordManager.LocalRecord> newRecordProperty() {
        return newRecord.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<RecordManager.LocalRecord> previousRecordProperty() {
        return previousRecord.getReadOnlyProperty();
    }

    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    public SimpleObjectProperty<Status> statusProperty() {
        return status;
    }

    public Enums.Button getButton() {
        return newRecord.get().button;
    }

    public Enums.Pattern getPattern() {
        return newRecord.get().pattern;
    }

    public enum Status {
        CANCELED,
        HIGHER_RECORD_EXISTS,
        NONE,
        READY,
        UPLOADED,
        UPLOADING
    }
}
