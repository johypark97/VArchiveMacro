package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.image.ImageConverter;
import java.awt.image.BufferedImage;
import java.io.IOException;

class ScanData {
    public enum Status {
        CACHED, CAPTURED, DISK_LOADED, DISK_SAVED, EXCEPTION, NONE,
    }


    private Exception exception;
    private Status status = Status.NONE;
    private byte[] imageBytes;
    private final ScanDataManager parent;
    protected final LocalSong song;
    public final int taskNumber;

    public ScanData(ScanDataManager parent, int taskNumber, LocalSong song) {
        this.parent = parent;
        this.song = song;
        this.taskNumber = taskNumber;
    }

    public void saveImage(BufferedImage image) throws IOException {
        imageBytes = ImageConverter.imageToPngBytes(image);
    }

    public BufferedImage loadImage() throws IOException {
        if (imageBytes == null) {
            throw new NullPointerException("image bytes is null");
        }

        return ImageConverter.pngBytesToImage(imageBytes);
    }

    public byte[] getImageBytes() {
        return (imageBytes != null) ? imageBytes.clone() : null;
    }

    public void setImageBytes(byte[] bytes) {
        imageBytes = bytes.clone();
    }

    public String getTitle() {
        return song.title();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        status = value;
        parent.notify_statusUpdated(taskNumber);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception value) {
        exception = value;
        setStatus(Status.EXCEPTION);
    }
}
