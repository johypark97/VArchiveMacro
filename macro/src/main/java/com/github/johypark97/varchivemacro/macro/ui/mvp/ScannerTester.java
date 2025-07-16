package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import javafx.scene.image.Image;

public interface ScannerTester {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void requestStopStage();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void setImage(Image image);
    }
}
