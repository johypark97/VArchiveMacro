package com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis.CaptureAnalysisTaskResult;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class ScannerAnalysisViewModel {
    public static class AnalysisResult {
        private final ReadOnlyIntegerWrapper captureEntryId = new ReadOnlyIntegerWrapper();
        private final ReadOnlyObjectWrapper<Exception> exception = new ReadOnlyObjectWrapper<>();
        private final ReadOnlyObjectWrapper<Status> status = new ReadOnlyObjectWrapper<>();

        public AnalysisResult(int captureEntryId, Status status, Exception exception) {
            this.captureEntryId.set(captureEntryId);
            this.exception.set(exception);
            this.status.set(status);
        }

        public static AnalysisResult from(CaptureAnalysisTaskResult result) {
            Status s = switch (result.getStatus()) {
                case ALREADY_DONE -> Status.ALREADY_DONE;
                case DONE -> Status.DONE;
                case ERROR -> Status.ERROR;
                case SUSPENDED -> Status.SUSPENDED;
            };

            return new AnalysisResult(result.captureEntryId, s, result.getException());
        }

        public ReadOnlyIntegerProperty captureEntryIdProperty() {
            return captureEntryId.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<Status> statusProperty() {
            return status.getReadOnlyProperty();
        }

        public ReadOnlyObjectProperty<Exception> exceptionProperty() {
            return exception.getReadOnlyProperty();
        }

        public boolean hasException() {
            return Status.ERROR.equals(status.get());
        }

        public boolean isAlreadyAnalyzed() {
            return Status.ALREADY_DONE.equals(status.get());
        }

        public enum Status {
            ALREADY_DONE("scanner.processor.analysis.result.status.alreadyDone"),
            DONE("scanner.processor.analysis.result.status.done"),
            ERROR("scanner.processor.analysis.result.status.error"),
            SUSPENDED("scanner.processor.analysis.result.status.suspended");

            private final String languageKey;

            Status(String languageKey) {
                this.languageKey = languageKey;
            }

            @Override
            public String toString() {
                return Language.INSTANCE.getString(languageKey);
            }
        }
    }
}
