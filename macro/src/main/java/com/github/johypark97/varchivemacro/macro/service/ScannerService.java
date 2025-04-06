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
import java.util.function.Consumer;

public interface ScannerService {
    void validateCacheDirectory(Path path) throws IOException;

    void setupService(Consumer<Throwable> onThrow);

    void startCollectionScan(Runnable onDone, Runnable onCancel);

    void stopCollectionScan();

    void startAnalysis(Consumer<Double> onUpdateProgress, Runnable onDataReady, Runnable onDone,
            Runnable onCancel);

    void stopAnalysis();

    void collectNewRecord(Runnable onDone);

    void startUpload(Runnable onDone, Runnable onCancel);

    void stopUpload();

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
