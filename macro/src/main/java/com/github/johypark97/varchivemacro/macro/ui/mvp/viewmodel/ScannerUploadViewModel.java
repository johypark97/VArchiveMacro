package com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel;

import com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload.NewRecordEntry;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ScannerUploadViewModel {
    public enum RecordButton {
        B4(4),
        B5(5),
        B6(6),
        B8(8);

        private final int value;

        RecordButton(int value) {
            this.value = value;
        }

        public static RecordButton from(
                com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton button) {
            return switch (button) {
                case B4 -> B4;
                case B5 -> B5;
                case B6 -> B6;
                case B8 -> B8;
            };
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }


    public enum RecordPattern {
        NM,
        HD,
        MX,
        SC;

        public static RecordPattern from(
                com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern pattern) {
            return switch (pattern) {
                case NORMAL -> NM;
                case HARD -> HD;
                case MAXIMUM -> MX;
                case SC -> SC;
            };
        }
    }


    public static class NewRecordData {
        private final int updatedSongRecordEntryId;

        private final int songId;
        private final String songTitle;
        private final String songComposer;
        private final String songPack;

        private final RecordButton button;
        private final RecordPattern pattern;

        private final SongRecord previousRecord;
        private final SongRecord newRecord;

        private final BooleanProperty selected = new SimpleBooleanProperty();

        private Runnable onSelectedChange;

        public NewRecordData(int updatedSongRecordEntryId, int songId, String songTitle,
                String songComposer, String songPack, RecordButton button, RecordPattern pattern,
                SongRecord previousRecord, SongRecord newRecord) {
            this.updatedSongRecordEntryId = updatedSongRecordEntryId;
            this.songId = songId;
            this.songTitle = songTitle;
            this.songComposer = songComposer;
            this.songPack = songPack;
            this.button = button;
            this.pattern = pattern;
            this.previousRecord = previousRecord;
            this.newRecord = newRecord;

            selected.addListener((observable, oldValue, newValue) -> {
                if (onSelectedChange != null) {
                    onSelectedChange.run();
                }
            });
        }

        public static NewRecordData from(NewRecordEntry newRecordEntry) {
            return new NewRecordData(newRecordEntry.updatedSongRecordEntryId(),
                    newRecordEntry.song().songId(), newRecordEntry.song().title(),
                    newRecordEntry.song().composer(), newRecordEntry.song().pack().name(),
                    RecordButton.from(newRecordEntry.button()),
                    RecordPattern.from(newRecordEntry.pattern()),
                    SongRecord.from(newRecordEntry.previousRecord()),
                    SongRecord.from(newRecordEntry.newRecord()));
        }

        public int getUpdatedSongRecordEntryId() {
            return updatedSongRecordEntryId;
        }

        public int getSongId() {
            return songId;
        }

        public String getSongTitle() {
            return songTitle;
        }

        public String getSongComposer() {
            return songComposer;
        }

        public String getSongPack() {
            return songPack;
        }

        public RecordButton getButton() {
            return button;
        }

        public RecordPattern getPattern() {
            return pattern;
        }

        public SongRecord getPreviousRecord() {
            return previousRecord;
        }

        public SongRecord getNewRecord() {
            return newRecord;
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public void setOnSelectedChange(Runnable value) {
            onSelectedChange = value;
        }
    }


    public record SongRecord(float rate, boolean maxCombo) implements Comparable<SongRecord> {
        public static SongRecord from(
                com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord songRecord) {
            return new SongRecord(songRecord.rate(), songRecord.maxCombo());
        }

        @Override
        public int compareTo(SongRecord o) {
            return Float.compare(rate, o.rate);
        }
    }
}
