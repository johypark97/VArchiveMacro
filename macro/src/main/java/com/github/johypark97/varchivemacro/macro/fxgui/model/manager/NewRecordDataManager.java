package com.github.johypark97.varchivemacro.macro.fxgui.model.manager;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class NewRecordDataManager {
    private final List<NewRecordData> newRecordDataList = new ArrayList<>();

    public List<NewRecordData> copyNewRecordDataList() {
        return List.copyOf(newRecordDataList);
    }

    public boolean isEmpty() {
        return newRecordDataList.isEmpty();
    }

    public void clear() {
        newRecordDataList.clear();
    }

    public NewRecordData createNewRecordData(Song song, LocalRecord previousRecord,
            LocalRecord newRecord) {
        int id = newRecordDataList.size();

        NewRecordData data = new NewRecordData(id, song, previousRecord, newRecord);
        newRecordDataList.add(data);

        return data;
    }

    public static class NewRecordData {
        private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();
        private final ReadOnlyObjectWrapper<Song> song = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<LocalRecord> newRecord = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<LocalRecord> previousRecord =
                new ReadOnlyObjectWrapper<>();

        public final SimpleBooleanProperty selected = new SimpleBooleanProperty();
        public final SimpleObjectProperty<Status> status = new SimpleObjectProperty<>(Status.NONE);

        public NewRecordData(int id, Song song, LocalRecord previousRecord, LocalRecord newRecord) {
            this.id.set(id);
            this.newRecord.set(newRecord);
            this.previousRecord.set(previousRecord.clone());
            this.song.set(song);
        }

        public ReadOnlyIntegerProperty idProperty() {
            return id.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<Song> songProperty() {
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
            CANCELED,
            HIGHER_RECORD_EXISTS,
            NONE,
            READY,
            UPLOADED,
            UPLOADING
        }
    }
}
