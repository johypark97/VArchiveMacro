package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import java.nio.file.Path;
import javafx.scene.image.Image;
import javafx.stage.Window;

public interface AnalysisDataViewer {
    interface AnalysisDataViewerPresenter extends MvpPresenter<AnalysisDataViewerView> {
        StartData getStartData();

        void setStartData(StartData value);

        void updateView();
    }


    interface AnalysisDataViewerView extends MvpView<AnalysisDataViewerPresenter> {
        void showError(String header, Throwable throwable);

        void setTitleData(Image image, String text);

        void setRecordBoxData(int row, int column, RecordBoxData data);
    }


    class StartData {
        public Path cacheDirectoryPath;
        public Window ownerWindow;
        public int analysisDataId;
    }


    class RecordBoxData {
        public Image maxComboImage;
        public Image rateImage;
        public String rateText;
        public boolean maxCombo;
    }
}
