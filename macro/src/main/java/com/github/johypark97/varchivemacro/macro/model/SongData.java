package com.github.johypark97.varchivemacro.macro.model;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
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
import javafx.collections.FXCollections;

public class SongData {
    private final ReadOnlyListWrapper<CaptureData> childList = new ReadOnlyListWrapper<>();
    private final ReadOnlyMapWrapper<CaptureData, LinkMetadata> linkMap =
            new ReadOnlyMapWrapper<>();

    private final ReadOnlyIntegerWrapper id = new ReadOnlyIntegerWrapper();
    private final ReadOnlyObjectWrapper<SongDatabase.Song> song = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper normalizedTitle = new ReadOnlyStringWrapper();

    public final SimpleBooleanProperty linkChanged = new SimpleBooleanProperty();
    public final SimpleBooleanProperty linkExact = new SimpleBooleanProperty();
    public final SimpleBooleanProperty selected = new SimpleBooleanProperty();

    public SongData(int id, SongDatabase.Song song, String normalizedTitle) {
        this.id.set(id);
        this.normalizedTitle.set(normalizedTitle);
        this.song.set(song);

        childList.set(FXCollections.observableArrayList());
        linkMap.set(FXCollections.observableHashMap());
    }

    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<SongDatabase.Song> songProperty() {
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

    public StringUtils.StringDiff diff(CaptureData captureData) {
        return new StringUtils.StringDiff(normalizedTitle.get(), captureData.scannedTitle.get());
    }

    @Override
    public String toString() {
        return String.format("SongData{%d, %d '%s'}", id.get(), song.get().id(),
                normalizedTitle.get());
    }
}
