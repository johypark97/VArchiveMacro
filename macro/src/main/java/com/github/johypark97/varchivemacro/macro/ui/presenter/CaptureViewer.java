package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonView;
import javafx.scene.image.Image;

public interface CaptureViewer {
    interface CaptureViewerPresenter
            extends CommonPresenter<CaptureViewerView, CaptureViewerPresenter> {
        void showImage(Image image);
    }


    interface CaptureViewerView extends CommonView<CaptureViewerView, CaptureViewerPresenter> {
        void startView(Image image);

        void setImage(Image image);
    }
}
