package com.github.johypark97.varchivemacro.macro.fxgui.model.manager;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils.StringDiff;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

public class ScanDataManager {
    private final ReadOnlyListWrapper<CaptureData> captureDataList = new ReadOnlyListWrapper<>();
    private final ReadOnlyListWrapper<SongData> songDataList = new ReadOnlyListWrapper<>();

    public ScanDataManager() {
        captureDataList.set(FXCollections.observableArrayList());
        songDataList.set(FXCollections.observableArrayList());
    }

    public ReadOnlyListProperty<CaptureData> captureDataListProperty() {
        return captureDataList.getReadOnlyProperty();
    }

    public ReadOnlyListProperty<SongData> songDataListProperty() {
        return songDataList.getReadOnlyProperty();
    }

    public void clear() {
        captureDataList.clear();
        songDataList.clear();
    }

    public SongData createSongData(LocalDlcSong song, String normalizedTitle) {
        int id = songDataList.size();

        SongData data = new SongData(id, song, normalizedTitle);
        songDataList.add(data);

        return data;
    }

    public CaptureData createCaptureData() {
        int id = captureDataList.size();

        CaptureData data = new CaptureData(id);
        captureDataList.add(data);

        return data;
    }

    public static class SongData {
        private final ReadOnlyListWrapper<CaptureData> childList = new ReadOnlyListWrapper<>();
        private final ReadOnlyMapWrapper<CaptureData, LinkMetadata> linkMap =
                new ReadOnlyMapWrapper<>();

        private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();
        private final ReadOnlyObjectWrapper<LocalDlcSong> song = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyStringWrapper normalizedTitle = new ReadOnlyStringWrapper();

        public final SimpleBooleanProperty linkChanged = new SimpleBooleanProperty();
        public final SimpleBooleanProperty linkExact = new SimpleBooleanProperty();
        public final SimpleBooleanProperty selected = new SimpleBooleanProperty();

        public SongData(int id, LocalDlcSong song, String normalizedTitle) {
            this.id.set(id);
            this.normalizedTitle.set(normalizedTitle);
            this.song.set(song);

            childList.set(FXCollections.observableArrayList());
            linkMap.set(FXCollections.observableHashMap());
        }

        public ReadOnlyIntegerProperty idProperty() {
            return id.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<LocalDlcSong> songProperty() {
            return song.getReadOnlyProperty();
        }

        public ReadOnlyStringProperty normalizedTitleProperty() {
            return normalizedTitle.getReadOnlyProperty();
        }

        public ReadOnlyListProperty<CaptureData> childListProperty() {
            return childList.getReadOnlyProperty();
        }

        public ReadOnlyMapProperty<CaptureData, LinkMetadata> linkMapProperty() {
            return linkMap.getReadOnlyProperty();
        }

        public SimpleBooleanProperty linkChangedProperty() {
            return linkChanged;
        }

        public SimpleBooleanProperty linkExactProperty() {
            return linkExact;
        }

        public SimpleBooleanProperty selectedProperty() {
            return selected;
        }

        public void link(CaptureData child) {
            if (childList.contains(child)) {
                return;
            }

            childList.add(child);
            linkMap.put(child, new LinkMetadata(this, child));
            child.onLink(this);
        }

        public void unlink(CaptureData child) {
            if (!childList.contains(child)) {
                return;
            }

            childList.remove(child);
            linkMap.remove(child);
            child.onUnlink(this);
        }

        public StringDiff diff(CaptureData captureData) {
            return new StringDiff(normalizedTitle.get(), captureData.scannedTitle.get());
        }

        @Override
        public String toString() {
            return String.format("SongData{%d, %d '%s'}", id.get(), song.get().id,
                    normalizedTitle.get());
        }
    }


    public static class CaptureData {
        private final ReadOnlyListWrapper<SongData> parentList = new ReadOnlyListWrapper<>();

        private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();

        public final SimpleObjectProperty<Exception> exception = new SimpleObjectProperty<>();
        public final SimpleStringProperty scannedTitle = new SimpleStringProperty();

        public CaptureData(int id) {
            this.id.set(id);

            parentList.set(FXCollections.observableArrayList());
        }

        public ReadOnlyIntegerProperty idProperty() {
            return id.getReadOnlyProperty();
        }

        public SimpleStringProperty scannedTitleProperty() {
            return scannedTitle;
        }

        public SimpleObjectProperty<Exception> exceptionProperty() {
            return exception;
        }

        public ReadOnlyListProperty<SongData> parentListProperty() {
            return parentList.getReadOnlyProperty();
        }

        private void onLink(SongData parent) {
            parentList.add(parent);
        }

        private void onUnlink(SongData parent) {
            parentList.remove(parent);
        }

        @Override
        public String toString() {
            return String.format("CaptureData{%d, '%s'}", id.get(), scannedTitle.get());
        }
    }


    public static class LinkMetadata {
        private final ReadOnlyDoubleWrapper accuracy = new ReadOnlyDoubleWrapper();
        private final ReadOnlyIntegerWrapper distance = new ReadOnlyIntegerWrapper();

        public LinkMetadata(SongData parent, CaptureData child) {
            StringDiff diff = parent.diff(child);

            accuracy.set(diff.getSimilarity());
            distance.set(diff.getDistance());
        }

        public ReadOnlyDoubleProperty accuracyProperty() {
            return accuracy.getReadOnlyProperty();
        }

        public ReadOnlyIntegerProperty distanceProperty() {
            return distance.getReadOnlyProperty();
        }

        @Override
        public String toString() {
            return String.format("LinkMetadata{%d, %.2f%%}", distance.get(), accuracy.get() * 100);
        }
    }
}
