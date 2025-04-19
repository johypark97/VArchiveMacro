package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javafx.concurrent.Task;

public interface CollectionScanService {
    void validateCacheDirectory(Path path) throws IOException;

    boolean isReady_collectionScan();

    Task<Void> createTask_collectionScan();

    void stopTask_collectionScan();

    void clearScanData(Runnable onClear);

    CaptureData getCaptureData(int id);

    List<CaptureData> copyCaptureDataList();

    SongData getSongData(int id);

    List<SongData> copySongDataList();

    BufferedImage getCaptureImage(int id) throws IOException;
}
