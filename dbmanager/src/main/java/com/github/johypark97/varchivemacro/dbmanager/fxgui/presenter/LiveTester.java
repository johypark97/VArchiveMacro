package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import java.nio.file.Path;
import javafx.scene.image.Image;

public interface LiveTester {
    interface LiveTesterPresenter extends MvpPresenter<LiveTesterView> {
        void focusView();

        void setStartData(StartData value);

        RecognizedData onRecognize();
    }


    interface LiveTesterView extends MvpView<LiveTesterPresenter> {
        void focusView();

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
