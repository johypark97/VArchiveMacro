package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import java.nio.file.Path;
import javafx.scene.image.Image;

public interface AnalysisDataViewer {
    interface AnalysisDataViewerPresenter
            extends MvpPresenter<AnalysisDataViewerView, AnalysisDataViewerPresenter> {
        void showAnalysisData(Path cacheDirectoryPath, int analysisDataId);
    }


    interface AnalysisDataViewerView
            extends MvpView<AnalysisDataViewerView, AnalysisDataViewerPresenter> {
        void showError(String header, Throwable throwable);

        void setSongText(String text);

        void setTitleImage(Image image);

        void setTitleText(String text);

        void setRecordBoxData(int row, int column, RecordBoxData data);
    }


    class RecordBoxData {
        public Image maxComboImage;
        public Image rateImage;
        public String rateText;
        public boolean maxCombo;
    }
}
