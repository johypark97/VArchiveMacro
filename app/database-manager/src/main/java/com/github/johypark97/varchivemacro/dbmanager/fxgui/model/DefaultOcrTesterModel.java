package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.core.ServiceManager;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrTesterService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrTesterService.Builder;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.task.OcrTester;
import java.nio.file.Path;
import java.util.Objects;
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
    public OcrTesterService.Builder setupTest() {
        return new Builder();
    }

    @Override
    public boolean startTest(Path cachePath, Path tessdataPath, String tessdataLanguage) {
        OcrTesterService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(OcrTesterService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(() -> {
            OcrTester task = new OcrTester();

            task.cachePath = cachePath;
            task.tessdataLanguage = tessdataLanguage;
            task.tessdataPath = tessdataPath;

            task.onAddData = x -> Platform.runLater(() -> getOcrTestDataList().add(x));
            task.onClearData = () -> Platform.runLater(() -> getOcrTestDataList().clear());

            return task;
        });

        service.reset();
        service.start();

        return true;
    }

    @Override
    public boolean stopTest() {
        return ModelHelper.stopService(OcrTesterService.class);
    }
}
