package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.DatabaseValidator;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor.RemoteValidator;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.DefaultSongViewModel;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class DbManagerPresenter implements Presenter {
    // view
    private final Class<? extends View> viewClass;
    private View view;

    // models
    private SongModel songModel;

    public DbManagerPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    public void setModels(SongModel songModel) {
        this.songModel = songModel;
    }

    private void newView() {
        try {
            view = viewClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    @Override
    public synchronized void start() {
        if (view != null) {
            return;
        }
        newView();

        view.showView();
    }

    @Override
    public void stop() {
        view.disposeView();
    }

    @Override
    public void loadDatabase(String path) {
        try {
            songModel.load(Path.of(path));
            view.setViewModels(new DefaultSongViewModel(songModel));
        } catch (IOException e) {
            view.showErrorDialog("Cannot read the file");
        } catch (RuntimeException e) {
            view.showErrorDialog(e.getMessage());
        }
    }

    @Override
    public void validateDatabase() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog("Database not loaded");
            return;
        }

        DatabaseValidator validator = new DatabaseValidator(songModel);
        view.setValidatorResultText(validator.validate());
    }

    @Override
    public void checkRemote() {
        if (!songModel.isLoaded()) {
            view.showErrorDialog("Database not loaded");
            return;
        }

        try {
            RemoteValidator validator = new RemoteValidator(songModel);
            view.setCheckerResultText(validator.validate());
        } catch (GeneralSecurityException e) {
            view.showErrorDialog("TLS Init Error");
        } catch (IOException e) {
            view.showErrorDialog(e.getMessage());
        } catch (InterruptedException ignored) {
        }
    }
}
