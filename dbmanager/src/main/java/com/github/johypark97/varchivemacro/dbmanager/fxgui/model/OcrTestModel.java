package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrTestService.Builder;
import java.nio.file.Path;
import javafx.collections.ObservableList;

public interface OcrTestModel {
    ObservableList<OcrTestData> getOcrTestDataList();

    Builder setupOcrTestService();

    boolean startOcrTestService(Path cachePath, Path tessdataPath, String tessdataLanguage);

    boolean stopOcrTestService();
}
