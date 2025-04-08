package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javafx.concurrent.Task;

public interface ScannerService {
    void validateCacheDirectory(Path path) throws IOException;

    Task<Void> createTask_collectionScan();

    void stopTask_collectionScan();

    Task<Void> createTask_analysis(Runnable onDataReady);

    void stopTask_analysis();

    Task<Void> createTask_collectNewRecord();

    Task<Void> createTask_startUpload();

    void stopTask_upload();

    boolean isScanDataEmpty();

    void clearScanData(Runnable onClear);

    boolean isAnalysisDataEmpty();

    void clearAnalysisData(Runnable onClear);

    boolean isNewRecordDataEmpty();

    CaptureData getCaptureData(int id);

    List<CaptureData> copyCaptureDataList();

    SongData getSongData(int id);

    List<SongData> copySongDataList();

    BufferedImage getCaptureImage(int id) throws IOException;

    List<AnalysisData> copyAnalysisDataList();

    AnalyzedRecordData getAnalyzedRecordData(int id) throws Exception;

    List<NewRecordData> copyNewRecordDataList();
}
