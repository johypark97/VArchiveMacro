package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.LiveTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import java.awt.AWTException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;

public class LiveTesterPresenterImpl
        extends AbstractMvpPresenter<LiveTesterPresenter, LiveTesterView>
        implements LiveTesterPresenter {
    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<LiveTesterModel> liveTesterModelReference;

    private StartData startData;

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
    protected LiveTesterPresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        Objects.requireNonNull(startData);
        Objects.requireNonNull(startData.tessdataLanguage);
        Objects.requireNonNull(startData.tessdataPath);

        if (startData.tessdataLanguage.isBlank()) {
            AlertBuilder.warning().setContentText(
                    "tessdataLanguage is blank. may not work properly.").alert.showAndWait();
        }

        List<Song> songList = getDatabaseModel().getSongList();
        TitleTool titleTool = getDatabaseModel().getTitleTool();

        try {
            getLiveTesterModel().initialize(songList, titleTool, startData);
        } catch (AWTException | NotSupportedResolutionException | OcrInitializationError e) {
            AlertBuilder.error().setThrowable(e).alert.showAndWait();
            return false;
        }

        return true;
    }

    @Override
    protected boolean terminate() {
        getLiveTesterModel().terminate();

        return true;
    }

    @Override
    public void focusView() {
        getView().focusView();
    }

    @Override
    public void setStartData(StartData value) {
        startData = value;
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
            AlertBuilder.error().setThrowable(e).alert.showAndWait();
            return null;
        }

        return viewData;
    }
}
