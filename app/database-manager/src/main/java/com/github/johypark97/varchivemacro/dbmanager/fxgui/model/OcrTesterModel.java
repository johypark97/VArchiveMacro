package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import javafx.collections.ObservableList;

public interface OcrTesterModel {
    ObservableList<OcrTestData> getOcrTestDataList();

    boolean runTest(List<LocalDlcSong> dlcSongList, TitleTool titleTool, Path cachePath,
            Path tessdataPath, String tessdataLanguage, Runnable onDone, Runnable onCancel,
            Consumer<Throwable> onThrow, Consumer<Double> onUpdateProgress);
}
