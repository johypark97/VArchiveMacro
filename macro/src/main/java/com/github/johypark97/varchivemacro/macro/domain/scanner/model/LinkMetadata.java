package com.github.johypark97.varchivemacro.macro.domain.scanner.model;

import com.github.johypark97.varchivemacro.lib.scanner.StringUtils;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

public class LinkMetadata {
    private final ReadOnlyDoubleWrapper accuracy = new ReadOnlyDoubleWrapper();
    private final ReadOnlyIntegerWrapper distance = new ReadOnlyIntegerWrapper();

    public LinkMetadata(SongData parent, CaptureData child) {
        StringUtils.StringDiff diff = parent.diff(child);

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
