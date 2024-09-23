package com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer.CaptureViewer.CaptureViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer.CaptureViewer.CaptureViewerView;
import javafx.scene.image.Image;

public class CaptureViewerPresenterImpl implements CaptureViewerPresenter {
    private final Runnable onStop;

    @MvpView
    public CaptureViewerView view;

    public CaptureViewerPresenterImpl(Runnable onStop) {
        this.onStop = onStop;
    }

    @Override
    public void onStartView() {
    }

    @Override
    public void onStopView() {
        view.getWindow().hide();

        onStop.run();
    }

    @Override
    public void showImage(Image image) {
        view.setImage(image);
    }
}
