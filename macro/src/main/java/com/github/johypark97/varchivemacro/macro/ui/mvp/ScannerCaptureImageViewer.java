package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.CaptureImageViewerViewModel;
import javafx.collections.ObservableList;

public interface ScannerCaptureImageViewer {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void requestStopStage();

        void updateFilter(String text);

        void showCaptureImage(int entryId);
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void setCaptureImageList(ObservableList<CaptureImageViewerViewModel.CaptureImage> value);

        void showCaptureImage(CaptureImageViewerViewModel.CaptureImageDetail value);
    }
}
