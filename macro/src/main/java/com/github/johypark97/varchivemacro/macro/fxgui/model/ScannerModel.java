package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javafx.collections.ObservableList;

public interface ScannerModel {
    void validateCacheDirectory(Path path) throws IOException;

    void setupService(Consumer<Throwable> onThrow);

    boolean startCollectionScan(Runnable onStart, Runnable onDone, Runnable onCancel,
            Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration);

    boolean stopCollectionScan();

    boolean starAnalysis(Runnable onDone, Runnable onCancel, Path cacheDirectoryPath);

    boolean stopAnalysis();

    boolean isScanDataEmpty();

    void clearScanData();

    boolean isAnalysisDataEmpty();

    void clearAnalysisData();

    ObservableList<CaptureData> getObservableCaptureDataList();

    ObservableList<SongData> getObservableSongDataList();

    BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException;

    ObservableList<AnalysisData> getObservableAnalysisDataList();

    AnalyzedRecordData getAnalyzedRecordData(Path cacheDirectoryPath, int id) throws Exception;

    class AnalyzedRecordData {
        public BufferedImage titleImage;
        public BufferedImage[][] maxComboImage = new BufferedImage[4][4];
        public BufferedImage[][] rateImage = new BufferedImage[4][4];
        public String titleText;
        public String[][] rateText = new String[4][4];
        public boolean[][] maxCombo = new boolean[4][4];
    }
}
