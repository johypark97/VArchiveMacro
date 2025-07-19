package com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.SongData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class ScannerReviewViewModel {
    public record CaptureData(int entryId, String scannedTitle) {
        public static CaptureData from(
                com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.CaptureData data) {
            return new CaptureData(data.entryId(), data.scannedTitle());
        }
    }


    public record LinkedCaptureData(CaptureData captureData, int distance) {
        public static LinkedCaptureData from(SongData.LinkedCaptureData data) {
            return new LinkedCaptureData(CaptureData.from(data.captureData()), data.distance());
        }
    }


    public static class LinkTableData {
        private final ReadOnlyIntegerWrapper songId = new ReadOnlyIntegerWrapper();
        private final ReadOnlyObjectWrapper<Accuracy> accuracy = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyStringWrapper songComposer = new ReadOnlyStringWrapper();
        private final ReadOnlyStringWrapper songPack = new ReadOnlyStringWrapper();
        private final ReadOnlyStringWrapper songTitle = new ReadOnlyStringWrapper();

        private final BooleanProperty selected = new SimpleBooleanProperty();
        private final ListProperty<ScannerReviewViewModel.LinkedCaptureData> linkedCaptureDataList =
                new SimpleListProperty<>();
        private final ObjectProperty<Problem> problem = new SimpleObjectProperty<>(Problem.NONE);

        private Runnable onSelectedChange;

        public LinkTableData(int songId, String songTitle, String songComposer, String songPack,
                ScannerReviewViewModel.LinkedCaptureData... linkedCaptureDataArray) {
            this.songComposer.set(songComposer);
            this.songId.set(songId);
            this.songPack.set(songPack);
            this.songTitle.set(songTitle);

            linkedCaptureDataList.set(FXCollections.observableArrayList(linkedCaptureDataArray));

            switch (linkedCaptureDataList.size()) {
                case 0 -> accuracy.set(Accuracy.NOT_DETECTED);
                case 1 -> {
                    if (linkedCaptureDataList.getFirst().distance() == 0) {
                        accuracy.set(Accuracy.EXACT);
                        selected.set(true);
                    } else {
                        accuracy.set(Accuracy.SIMILAR);
                        problem.set(Problem.EDIT_NEEDED);
                    }
                }
                default -> {
                    accuracy.set(
                            linkedCaptureDataList.stream().map(x -> x.captureData().scannedTitle())
                                    .distinct().count() == 1
                                    ? Accuracy.DUPLICATED
                                    : Accuracy.CONFLICT);
                    problem.set(Problem.EDIT_NEEDED);
                }
            }

            selected.addListener((observable, oldValue, newValue) -> {
                // constraints
                if (linkedCaptureDataList.size() != 1) {
                    if (!oldValue && newValue) {
                        selected.set(false);
                    }

                    return;
                }

                if (onSelectedChange != null) {
                    onSelectedChange.run();
                }
            });
        }

        public static LinkTableData form(SongData data) {
            return new ScannerReviewViewModel.LinkTableData(data.song.songId(), data.song.title(),
                    data.song.composer(), data.song.pack().name(),
                    data.getAllLinkedCaptureData().stream()
                            .map(ScannerReviewViewModel.LinkedCaptureData::from)
                            .toArray(ScannerReviewViewModel.LinkedCaptureData[]::new));
        }

        public ReadOnlyIntegerProperty songIdProperty() {
            return songId.getReadOnlyProperty();
        }

        public ReadOnlyStringProperty songTitleProperty() {
            return songTitle.getReadOnlyProperty();
        }

        public ReadOnlyStringProperty songComposerProperty() {
            return songComposer.getReadOnlyProperty();
        }

        public ReadOnlyStringProperty songPackProperty() {
            return songPack.getReadOnlyProperty();
        }

        public ListProperty<ScannerReviewViewModel.LinkedCaptureData> captureImageProperty() {
            return linkedCaptureDataList;
        }

        public ReadOnlyObjectProperty<Accuracy> accuracyProperty() {
            return accuracy.getReadOnlyProperty();
        }

        public ObjectProperty<Problem> problemProperty() {
            return problem;
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public void setOnSelectedChange(Runnable value) {
            onSelectedChange = value;
        }

        public enum Accuracy {
            CONFLICT("scanner.processor.review.accuracy.conflict"),
            DUPLICATED("scanner.processor.review.accuracy.duplicated"),
            EXACT("scanner.processor.review.accuracy.exact"),
            NOT_DETECTED("scanner.processor.review.accuracy.notDetected"),
            SIMILAR("scanner.processor.review.accuracy.similar");

            private final String languageKey;

            Accuracy(String languageKey) {
                this.languageKey = languageKey;
            }

            @Override
            public String toString() {
                return Language.INSTANCE.getString(languageKey);
            }
        }


        public enum Problem {
            DELETED("scanner.processor.review.problem.deleted"),
            EDITED("scanner.processor.review.problem.edited"),
            EDIT_NEEDED("scanner.processor.review.problem.editNeeded"),
            NONE("");

            private final String languageKey;

            Problem(String languageKey) {
                this.languageKey = languageKey;
            }

            @Override
            public String toString() {
                return languageKey.isEmpty() ? "" : Language.INSTANCE.getString(languageKey);
            }
        }
    }
}
