package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonView;
import javafx.scene.image.Image;

public interface AnalysisDataViewer {
    interface AnalysisDataViewerPresenter
            extends CommonPresenter<AnalysisDataViewerView, AnalysisDataViewerPresenter> {
        void showAnalysisData(int analysisDataId);
    }


    interface AnalysisDataViewerView
            extends CommonView<AnalysisDataViewerView, AnalysisDataViewerPresenter> {
        void startView(int analysisDataId);

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
