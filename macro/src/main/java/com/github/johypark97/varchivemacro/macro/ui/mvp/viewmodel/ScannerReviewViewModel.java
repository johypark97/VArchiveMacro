package com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.SongData;
import java.util.EnumSet;
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
        public static LinkedCaptureData from(
                com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.LinkedCaptureData data) {
            return new LinkedCaptureData(CaptureData.from(data.captureData()), data.distance());
        }
    }


    public static class LinkTableData {
        private static final EnumSet<Accuracy> SELECTED_ACCURACY_CONSTRAINT =
                EnumSet.of(Accuracy.EXACT, Accuracy.SIMILAR);

        private final ReadOnlyIntegerWrapper songId = new ReadOnlyIntegerWrapper();
        private final ReadOnlyObjectWrapper<Accuracy> accuracy = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyStringWrapper songComposer = new ReadOnlyStringWrapper();
        private final ReadOnlyStringWrapper songPack = new ReadOnlyStringWrapper();
        private final ReadOnlyStringWrapper songTitle = new ReadOnlyStringWrapper();

        private final BooleanProperty selected = new SimpleBooleanProperty();
        private final ListProperty<LinkedCaptureData> linkedCaptureDataList =
                new SimpleListProperty<>();
        private final ObjectProperty<Problem> problem = new SimpleObjectProperty<>(Problem.NONE);

        private Runnable onSelectedChange;

        public LinkTableData(int songId, String songTitle, String songComposer, String songPack,
                Accuracy accuracyValue, LinkedCaptureData... linkedCaptureDataArray) {
            this.songComposer.set(songComposer);
            this.songId.set(songId);
            this.songPack.set(songPack);
            this.songTitle.set(songTitle);

            linkedCaptureDataList.set(FXCollections.observableArrayList(linkedCaptureDataArray));

            accuracy.set(accuracyValue);
            if (Accuracy.EXACT.equals(accuracy.get())) {
                selected.set(true);
            } else {
                problem.set(Problem.EDIT_NEEDED);
            }

            selected.addListener((observable, oldValue, newValue) -> {
                // constraints
                if (!SELECTED_ACCURACY_CONSTRAINT.contains(accuracy.get())
                        && !Problem.EDITED.equals(problem.get())) {
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
            Accuracy accuracyValue = switch (data.getLinkStatus()) {
                case CONFLICT -> Accuracy.CONFLICT;
                case DUPLICATED -> Accuracy.DUPLICATED;
                case EXACT -> Accuracy.EXACT;
                case NOT_DETECTED -> Accuracy.NOT_DETECTED;
                case SIMILAR -> Accuracy.SIMILAR;
            };

            return new LinkTableData(data.getSong().songId(), data.getSong().title(),
                    data.getSong().composer(), data.getSong().pack().name(), accuracyValue,
                    data.getLinkedCaptureDataList().stream().map(LinkedCaptureData::from)
                            .toArray(LinkedCaptureData[]::new));
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

        public ListProperty<LinkedCaptureData> captureImageProperty() {
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
