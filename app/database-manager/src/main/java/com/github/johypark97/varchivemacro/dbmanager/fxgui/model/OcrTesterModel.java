package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrTesterService.Builder;
import java.nio.file.Path;
import javafx.collections.ObservableList;

public interface OcrTesterModel {
    ObservableList<OcrTestData> getOcrTestDataList();

    Builder setupTest();

    boolean startTest(Path cachePath, Path tessdataPath, String tessdataLanguage);

    boolean stopTest();
}
