package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.core.GlobalExecutor;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task.OcrTester;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DefaultOcrTesterModel implements OcrTesterModel {
    public ObservableList<OcrTestData> ocrTestDataList;

    @Override
    public ObservableList<OcrTestData> getOcrTestDataList() {
        if (ocrTestDataList == null) {
            ocrTestDataList = FXCollections.observableArrayList();
        }

        return ocrTestDataList;
    }

    @Override
    public boolean runTest(List<LocalDlcSong> dlcSongList, TitleTool titleTool, Path cachePath,
            Path tessdataPath, String tessdataLanguage, Runnable onDone, Runnable onCancel,
            Consumer<Throwable> onThrow, Consumer<Double> onUpdateProgress) {
        return GlobalExecutor.getInstance().use(executorService -> {
            OcrTester ocrTester = new OcrTester();

            ocrTester.cachePath = cachePath;
            ocrTester.songList = dlcSongList;
            ocrTester.tessdataLanguage = tessdataLanguage;
            ocrTester.tessdataPath = tessdataPath;
            ocrTester.titleTool = titleTool;

            ocrTester.onAddData = x -> Platform.runLater(() -> getOcrTestDataList().add(x));
            ocrTester.onClearData = () -> Platform.runLater(() -> getOcrTestDataList().clear());
            ocrTester.onUpdateProgress = onUpdateProgress;

            CompletableFuture.runAsync(ocrTester, executorService)
                    .whenComplete(ModelHelper.defaultWhenComplete(onDone, onCancel, onThrow));
        });
    }
}
