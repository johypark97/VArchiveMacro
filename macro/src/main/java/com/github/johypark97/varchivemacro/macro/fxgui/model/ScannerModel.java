package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface ScannerModel {
    void validateCacheDirectory(Path path) throws IOException;

    void setupService(Runnable onDone, Runnable onCancel, Consumer<Throwable> onThrow);

    boolean startCollectionScan(Map<String, List<LocalDlcSong>> dlcTapSongMap, TitleTool titleTool,
            Set<String> selectedTabSet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration);

    boolean stopCollectionScan();
}
