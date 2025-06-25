package com.github.johypark97.varchivemacro.macro.ui.viewmodel;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ScannerScannerViewModel {
    public static class CategoryData {
        public final BooleanProperty selected = new SimpleBooleanProperty();
        public final Song.Pack.Category category;

        public CategoryData(Song.Pack.Category category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return category.name();
        }
    }
}
