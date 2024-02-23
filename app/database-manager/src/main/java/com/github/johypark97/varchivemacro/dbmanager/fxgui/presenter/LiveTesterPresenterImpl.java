package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.Dialogs;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.LiveTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import java.awt.AWTException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class LiveTesterPresenterImpl
        extends AbstractMvpPresenter<LiveTesterPresenter, LiveTesterView>
        implements LiveTesterPresenter {
    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<LiveTesterModel> liveTesterModelReference;

    public void linkModel(DatabaseModel databaseModel, LiveTesterModel liveTesterModel) {
        databaseModelReference = new WeakReference<>(databaseModel);
        liveTesterModelReference = new WeakReference<>(liveTesterModel);
    }

    public DatabaseModel getDatabaseModel() {
        return databaseModelReference.get();
    }

    public LiveTesterModel getLiveTesterModel() {
        return liveTesterModelReference.get();
    }

    @Override
    public boolean initialize(StartData data) {
        List<LocalDlcSong> dlcSongList = getDatabaseModel().getDlcSongList();
        TitleTool titleTool = getDatabaseModel().getTitleTool();

        try {
            getLiveTesterModel().initialize(dlcSongList, titleTool, data);
        } catch (AWTException | NotSupportedResolutionException | OcrInitializationError e) {
            Dialogs.showException(e);
            return false;
        }

        return true;
    }

    @Override
    public boolean terminate() {
        getLiveTesterModel().terminate();

        return true;
    }

    @Override
    public LiveTester.RecognizedData onRecognize() {
        LiveTester.RecognizedData viewData = new LiveTester.RecognizedData();
        try {
            LiveTesterModel.RecognizedData modelData = getLiveTesterModel().recognize();

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
