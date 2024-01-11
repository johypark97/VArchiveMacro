package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.Dialogs;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpPresenter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import javafx.stage.DirectoryChooser;

public class HomePresenterImpl extends AbstractMvpPresenter<HomePresenter, HomeView>
        implements HomePresenter {
    private static final Path INITIAL_DIRECTORY = Path.of("").toAbsolutePath();

    public DatabaseModel databaseModel;

    public HomePresenterImpl(Supplier<HomeView> viewConstructor) {
        super(viewConstructor);
    }

    public void setModel(DatabaseModel databaseModel) {
        this.databaseModel = databaseModel;
    }

    private Path openDirectorySelector() {
        String TITLE = "Select database directory";

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        directoryChooser.setTitle(TITLE);

        File file = directoryChooser.showDialog(null);
        if (file == null) {
            return null;
        }

        return file.toPath();
    }

    @Override
    protected HomePresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        Path path = openDirectorySelector();
        if (path == null) {
            return false;
        }

        try {
            databaseModel.load(path);
        } catch (IOException | RuntimeException e) {
            Dialogs.showException(e);
            throw new RuntimeException(e);
        }

        return true;
    }
}
