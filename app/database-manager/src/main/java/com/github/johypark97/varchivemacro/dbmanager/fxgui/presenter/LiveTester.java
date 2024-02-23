package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.common.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpView;
import java.nio.file.Path;
import javafx.scene.image.Image;

public interface LiveTester {
    interface LiveTesterPresenter extends MvpPresenter<LiveTesterView> {
        boolean initialize(StartData data);

        boolean terminate();

        RecognizedData onRecognize();
    }


    interface LiveTesterView extends MvpView<LiveTesterPresenter> {
        void setStartData(StartData value);

        void recognize();
    }


    class StartData {
        public Path tessdataPath;
        public String tessdataLanguage;
    }


    class RecognizedData {
        public Image image;
        public String recognized;
        public String text;
    }
}
