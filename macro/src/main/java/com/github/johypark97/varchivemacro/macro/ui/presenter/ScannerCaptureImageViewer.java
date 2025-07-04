package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.CaptureImageViewerViewModel;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public interface ScannerCaptureImageViewer {
    interface ScannerCaptureImageViewerPresenter extends
            Mvp.MvpPresenter<ScannerCaptureImageViewerView, ScannerCaptureImageViewerPresenter> {
        void startView();

        void requestStopStage();

        void updateFilter(String text);

        void showCaptureImage(int entryId);
    }


    interface ScannerCaptureImageViewerView
            extends Mvp.MvpView<ScannerCaptureImageViewerView, ScannerCaptureImageViewerPresenter> {
        void setCaptureImageList(ObservableList<CaptureImageViewerViewModel.CaptureImage> value);

        void showCaptureImage(Image value);
    }
}
