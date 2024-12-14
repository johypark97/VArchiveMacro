package com.github.johypark97.varchivemacro.macro.fxgui.model.manager;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class AnalysisDataManager {
    private final List<AnalysisData> analysisDataList = new ArrayList<>();

    public List<AnalysisData> copyAnalysisDataList() {
        return List.copyOf(analysisDataList);
    }

    public boolean isEmpty() {
        return analysisDataList.isEmpty();
    }

    public void clear() {
        analysisDataList.clear();
    }

    public AnalysisData getAnalysisData(int index) {
        return analysisDataList.get(index);
    }

    public AnalysisData createAnalysisData(SongData songData) {
        int id = analysisDataList.size();

        AnalysisData data = new AnalysisData(id, songData);
        analysisDataList.add(data);

        return data;
    }

    public static class AnalysisData {
        public final Table<Button, Pattern, RecordData> recordDataTable = HashBasedTable.create();

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


    public static class RecordData {
        public final SimpleBooleanProperty maxCombo = new SimpleBooleanProperty();
        public final SimpleStringProperty rateText = new SimpleStringProperty();
    }
}
