package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import javafx.scene.image.Image;

public interface CaptureViewer {
    interface CaptureViewerPresenter
            extends MvpPresenter<CaptureViewerView, CaptureViewerPresenter> {
    }


    interface CaptureViewerView extends MvpView<CaptureViewerView, CaptureViewerPresenter> {
        void setImage(Image image);
    }
}
