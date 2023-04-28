package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import javax.imageio.ImageIO;

class ScannerTask {
    public enum Status {
        ANALYZED, ANALYZING, CACHED, CAPTURED, DISK_LOADED, DISK_SAVED, EXCEPTION, NONE, WAITING
    }


    public static class AnalyzedData {
        public String rateText = "";
        public boolean isMaxCombo;
        public float rate;
    }


    private static final String FORMAT = "png";

    private final ScannerTaskManager manager;
    public final LocalSong song;
    public final int songCount;
    public final int songIndex;
    public final int taskNumber;

    private Exception exception;
    private Status status = Status.NONE;
    private final Table<Button, Pattern, AnalyzedData> analyzedDataList = HashBasedTable.create();
    public final Path filePath;

    public ScannerTask(ScannerTaskManager manager, int taskNumber, LocalSong song, int songIndex,
            int songCount, Path dirPath) {
        this.manager = manager;
        this.song = song;
        this.songCount = songCount;
        this.songIndex = songIndex;
        this.taskNumber = taskNumber;

        filePath = dirPath.resolve(String.format("%04d.%s", song.id(), FORMAT));
    }

    public void saveImage(BufferedImage image) throws IOException {
        Path dirPath = filePath.getParent();
        if (dirPath != null) {
            Files.createDirectories(dirPath);
        }

        Files.deleteIfExists(filePath);
        ImageIO.write(image, FORMAT, filePath.toFile());
    }

    public BufferedImage loadImage() throws IOException {
        return ImageIO.read(filePath.toFile());
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

    public void clearAnalyzedData() {
        analyzedDataList.clear();
    }

    public AnalyzedData getAnalyzedData(Button button, Pattern pattern) {
        return analyzedDataList.get(button, pattern);
    }

    public void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data) {
        analyzedDataList.put(button, pattern, data);
    }

    public Set<Cell<Button, Pattern, AnalyzedData>> getAnalyzedDataCellSet() {
        return analyzedDataList.cellSet();
    }
}
