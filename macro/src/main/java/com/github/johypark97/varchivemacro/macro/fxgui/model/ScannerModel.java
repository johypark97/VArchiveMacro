package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
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

    void setupService(Runnable onStart, Runnable onDone, Runnable onCancel,
            Consumer<Throwable> onThrow);

    boolean startCollectionScan(Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration);

    boolean stopCollectionScan();

    boolean isScanDataEmpty();

    void clearScanData();

    ObservableList<CaptureData> getObservableCaptureDataList();

    ObservableList<SongData> getObservableSongDataList();

    BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException;
}
