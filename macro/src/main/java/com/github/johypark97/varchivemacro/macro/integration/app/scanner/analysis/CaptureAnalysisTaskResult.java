package com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis;

public class CaptureAnalysisTaskResult {
    private Exception exception;
    private Status status = Status.SUSPENDED;

    public final int entryId;

    public CaptureAnalysisTaskResult(int entryId) {
        this.entryId = entryId;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception value) {
        this.exception = value;

        status = Status.ERROR;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public enum Status {
        DONE,
        ERROR,
        SUSPENDED
    }
}
