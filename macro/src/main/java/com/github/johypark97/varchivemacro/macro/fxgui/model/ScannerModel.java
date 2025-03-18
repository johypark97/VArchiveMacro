package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface ScannerModel {
    void validateCacheDirectory(Path path) throws IOException;

    void setupService(Consumer<Throwable> onThrow);

    void startCollectionScan(Runnable onDone, Runnable onCancel,
            Map<String, List<Song>> categoryNameSongListMap, TitleTool titleTool,
            Set<String> selectedCategorySet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration);

    void stopCollectionScan();

    void starAnalysis(Consumer<Double> onUpdateProgress, Runnable onDataReady, Runnable onDone,
            Runnable onCancel, Path cacheDirectoryPath, int analysisThreadCount);

    void stopAnalysis();

    void collectNewRecord(Runnable onDone, RecordRepository recordRepository);

    void startUpload(Runnable onDone, Runnable onCancel, DatabaseRepository databaseRepository,
            RecordRepository recordRepository, Path accountPath, int recordUploadDelay);

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

    BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException;

    List<AnalysisData> copyAnalysisDataList();

    AnalyzedRecordData getAnalyzedRecordData(Path cacheDirectoryPath, int id) throws Exception;

    List<NewRecordData> copyNewRecordDataList();

    class AnalyzedRecordData {
        public BufferedImage titleImage;
        public BufferedImage[][] maxComboImage = new BufferedImage[4][4];
        public BufferedImage[][] rateImage = new BufferedImage[4][4];
        public Song song;
        public String titleText;
        public String[][] rateText = new String[4][4];
        public boolean[][] maxCombo = new boolean[4][4];
    }
}
