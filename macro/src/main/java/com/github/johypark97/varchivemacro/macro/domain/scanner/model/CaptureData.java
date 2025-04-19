package com.github.johypark97.varchivemacro.macro.domain.scanner.model;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

public class CaptureData {
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

    protected void onLink(SongData parent) {
        parentList.add(parent);
    }

    protected void onUnlink(SongData parent) {
        parentList.remove(parent);
    }

    @Override
    public String toString() {
        return String.format("CaptureData{%d, '%s'}", id.get(), scannedTitle.get());
    }
}
