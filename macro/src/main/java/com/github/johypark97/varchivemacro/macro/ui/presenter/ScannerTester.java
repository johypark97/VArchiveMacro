package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import javafx.scene.image.Image;

public interface ScannerTester {
    interface ScannerTesterPresenter
            extends Mvp.MvpPresenter<ScannerTesterView, ScannerTesterPresenter> {
        void startView();

        void requestStopStage();
    }


    interface ScannerTesterView extends Mvp.MvpView<ScannerTesterView, ScannerTesterPresenter> {
        void setImage(Image image);
    }
}
