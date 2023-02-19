package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

class CaptureTask {
    public enum Status {
        CACHED, CAPTURED, DISK_LOADED, DISK_SAVED, EXCEPTION, NONE
    }


    public static final Path BASE_PATH = Path.of(System.getProperty("user.dir"), "cache/image");
    public static final String FORMAT = "png";

    private final CaptureTaskManager manager;
    private final LocalSong song;
    public final int taskNumber;

    private Exception exception;
    private Status status = Status.NONE;

    public CaptureTask(CaptureTaskManager manager, int taskNumber, LocalSong song) {
        this.manager = manager;
        this.song = song;
        this.taskNumber = taskNumber;
    }

    public Path getFilePath() {
        return BASE_PATH.resolve(String.format("%04d.%s", song.id(), FORMAT));
    }

    public void saveImage(BufferedImage image) throws IOException {
        Path path = getFilePath();
        Path dirPath = path.getParent();
        if (dirPath != null) {
            Files.createDirectories(dirPath);
        }

        Files.deleteIfExists(path);
        ImageIO.write(image, FORMAT, path.toFile());
    }

    public BufferedImage loadImage() throws IOException {
        return ImageIO.read(getFilePath().toFile());
    }

    public int getSongId() {
        return song.id();
    }

    public String getSongTitle() {
        return song.title();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status value) {
        status = value;
        manager.notify_statusUpdated(taskNumber);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception value) {
        exception = value;
        setStatus(Status.EXCEPTION);
    }
}
