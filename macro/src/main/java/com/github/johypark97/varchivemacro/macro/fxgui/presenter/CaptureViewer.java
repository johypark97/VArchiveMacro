package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import javafx.scene.image.Image;
import javafx.stage.Window;

public interface CaptureViewer {
    interface CaptureViewerPresenter extends MvpPresenter<CaptureViewerView> {
        StartData getStartData();

        void setStartData(StartData value);

        void updateView();
    }


    interface CaptureViewerView extends MvpView<CaptureViewerPresenter> {
        void showImage(Image image);
    }


    class StartData {
        public Image image;
        public Window ownerWindow;
    }
}
