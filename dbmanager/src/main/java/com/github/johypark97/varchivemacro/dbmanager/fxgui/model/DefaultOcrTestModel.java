package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.core.ServiceManager;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrTestService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrTestService.Builder;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrTestTask;
import java.nio.file.Path;
import java.util.Objects;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DefaultOcrTestModel implements OcrTestModel {
    public ObservableList<OcrTestData> ocrTestDataList;

    @Override
    public ObservableList<OcrTestData> getOcrTestDataList() {
        if (ocrTestDataList == null) {
            ocrTestDataList = FXCollections.observableArrayList();
        }

        return ocrTestDataList;
    }

    @Override
    public OcrTestService.Builder setupOcrTestService() {
        return new Builder();
    }

    @Override
    public boolean startOcrTestService(Path cachePath, Path tessdataPath, String tessdataLanguage) {
        OcrTestService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(OcrTestService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(() -> {
            OcrTestTask task = new OcrTestTask();

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
    public boolean stopOcrTestService() {
        return ModelHelper.stopService(OcrTestService.class);
    }
}
