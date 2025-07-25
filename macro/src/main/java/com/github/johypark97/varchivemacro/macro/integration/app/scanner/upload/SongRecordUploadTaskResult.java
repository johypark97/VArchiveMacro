package com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload;

public class SongRecordUploadTaskResult {
    private Status status = Status.SUSPENDED;

    public final int entryId;

    public SongRecordUploadTaskResult(int entryId) {
        this.entryId = entryId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        this.status = value;
    }

    public enum Status {
        HIGHER_RECORD_EXISTS,
        SUSPENDED,
        UPDATED
    }
}
