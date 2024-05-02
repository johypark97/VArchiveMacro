package com.github.johypark97.varchivemacro.macro.fxgui.model.manager;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class NewRecordDataManager {
    private final ReadOnlyMapWrapper<Integer, NewRecordData> newRecordDataMap =
            new ReadOnlyMapWrapper<>();

    public NewRecordDataManager() {
        newRecordDataMap.set(FXCollections.observableHashMap());
    }

    public ReadOnlyMapProperty<Integer, NewRecordData> newRecordDataMapProperty() {
        return newRecordDataMap.getReadOnlyProperty();
    }

    public boolean isEmpty() {
        return newRecordDataMap.isEmpty();
    }

    public void clear() {
        newRecordDataMap.clear();
    }

    public NewRecordData createNewRecordData(LocalDlcSong song, LocalRecord previousRecord,
            LocalRecord newRecord) {
        int id = newRecordDataMap.size();

        NewRecordData data = new NewRecordData(id, song, previousRecord, newRecord);
        newRecordDataMap.put(id, data);

        return data;
    }

    public static class NewRecordData {
        private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();
        private final ReadOnlyObjectWrapper<LocalDlcSong> song = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<LocalRecord> newRecord = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<LocalRecord> previousRecord =
                new ReadOnlyObjectWrapper<>();

        public final SimpleBooleanProperty selected = new SimpleBooleanProperty();
        public final SimpleObjectProperty<Status> status = new SimpleObjectProperty<>(Status.NONE);

        public NewRecordData(int id, LocalDlcSong song, LocalRecord previousRecord,
                LocalRecord newRecord) {
            this.id.set(id);
            this.newRecord.set(newRecord);
            this.previousRecord.set(previousRecord.clone());
            this.song.set(song);
        }

        public ReadOnlyIntegerProperty idProperty() {
            return id.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<LocalDlcSong> songProperty() {
            return song.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<LocalRecord> newRecordProperty() {
            return newRecord.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<LocalRecord> previousRecordProperty() {
            return previousRecord.getReadOnlyProperty();
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }

        public SimpleObjectProperty<Status> statusProperty() {
            return status;
        }

        public Button getButton() {
            return newRecord.get().button;
        }

        public Pattern getPattern() {
            return newRecord.get().pattern;
        }

        public enum Status {
            CANCELED, HIGHER_RECORD_EXISTS, NONE, READY, UPLOADED, UPLOADING
        }
    }
}
