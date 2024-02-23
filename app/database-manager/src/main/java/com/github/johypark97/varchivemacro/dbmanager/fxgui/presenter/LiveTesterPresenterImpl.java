package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.Dialogs;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.LiveTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import java.awt.AWTException;
import java.io.IOException;

public class LiveTesterPresenterImpl
        extends AbstractMvpPresenter<LiveTesterPresenter, LiveTesterView>
        implements LiveTesterPresenter {
    public LiveTesterModel liveTesterModel;

    public void setModel(LiveTesterModel liveTesterModel) {
        this.liveTesterModel = liveTesterModel;
    }

    @Override
    public boolean initialize(StartData data) {
        try {
            liveTesterModel.initialize(data);
        } catch (AWTException | NotSupportedResolutionException | OcrInitializationError e) {
            Dialogs.showException(e);
            return false;
        }

        return true;
    }

    @Override
    public boolean terminate() {
        liveTesterModel.terminate();

        return true;
    }

    @Override
    public LiveTester.RecognizedData onRecognize() {
        LiveTester.RecognizedData viewData = new LiveTester.RecognizedData();
        try {
            LiveTesterModel.RecognizedData modelData = liveTesterModel.recognize();

            viewData.image = modelData.image;
            viewData.recognized = modelData.recognized;
            viewData.text = modelData.text;
        } catch (IOException | PixError e) {
            Dialogs.showException(e);
            return null;
        }

        return viewData;
    }
}
