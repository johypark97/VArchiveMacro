package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.collections.ObservableMap;

public interface ScannerModel {
    void validateCacheDirectory(Path path) throws IOException;

    void setupService(Consumer<Throwable> onThrow);

    void startCollectionScan(Runnable onDone, Runnable onCancel,
            Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration);

    void stopCollectionScan();

    void starAnalysis(Runnable onDone, Runnable onCancel, Path cacheDirectoryPath);

    void stopAnalysis();

    void collectNewRecord(RecordModel recordModel);

    void startUpload(Runnable onDone, Runnable onCancel, DatabaseModel databaseModel,
            RecordModel recordModel, Path accountPath, int recordUploadDelay);

    void stopUpload();

    boolean isScanDataEmpty();

    void clearScanData(Runnable onClear);

    boolean isAnalysisDataEmpty();

    void clearAnalysisData();

    boolean isNewRecordDataEmpty();

    ObservableMap<Integer, CaptureData> getObservableCaptureDataMap();

    ObservableMap<Integer, SongData> getObservableSongDataMap();

    BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException;

    ObservableMap<Integer, AnalysisData> getObservableAnalysisDataMap();

    AnalyzedRecordData getAnalyzedRecordData(Path cacheDirectoryPath, int id) throws Exception;

    ObservableMap<Integer, NewRecordData> getObservableNewRecordDataMap();

    class AnalyzedRecordData {
        public BufferedImage titleImage;
        public BufferedImage[][] maxComboImage = new BufferedImage[4][4];
        public BufferedImage[][] rateImage = new BufferedImage[4][4];
        public String titleText;
        public String[][] rateText = new String[4][4];
        public boolean[][] maxCombo = new boolean[4][4];
    }
}
