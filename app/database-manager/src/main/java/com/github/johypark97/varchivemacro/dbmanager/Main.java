package com.github.johypark97.varchivemacro.dbmanager;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.Dialogs;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultLiveTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultOcrTestModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultOcrToolModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.LiveTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrTestModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrToolModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTesterPresenterImpl;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.LiveTesterViewImpl;
import com.github.johypark97.varchivemacro.lib.common.FxHookWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final DatabaseModel databaseModel = new DefaultDatabaseModel();
    private final LiveTesterModel liveTesterModel = new DefaultLiveTesterModel();
    private final OcrTestModel ocrTestModel = new DefaultOcrTestModel();
    private final OcrToolModel ocrToolModel = new DefaultOcrToolModel();

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");

        launch(args);
    }

    private boolean loadDatabaseModel(DatabaseModel databaseModel) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Path.of("").toAbsolutePath().toFile());
        directoryChooser.setTitle("Select database directory");

        File file = directoryChooser.showDialog(null);
        if (file == null) {
            return false;
        }

        try {
            databaseModel.load(file.toPath());
        } catch (IOException | RuntimeException e) {
            LOGGER.atError().log("DatabaseModel exception", e);
            Dialogs.showException(e);
            return false;
        }

        return true;
    }

    @Override
    public void init() throws Exception {
        FxHookWrapper.disableLogging();
        FxHookWrapper.setEventDispatcher();
        FxHookWrapper.register();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (!loadDatabaseModel(databaseModel)) {
            Platform.exit();
            return;
        }

        LiveTesterPresenterImpl liveTesterPresenter = new LiveTesterPresenterImpl();
        liveTesterPresenter.linkModel(databaseModel, liveTesterModel);

        LiveTesterViewImpl liveTesterView = new LiveTesterViewImpl();
        liveTesterView.linkPresenter(liveTesterPresenter);

        HomePresenterImpl homePresenter = new HomePresenterImpl();
        homePresenter.linkModel(databaseModel, ocrTestModel, ocrToolModel);

        HomeViewImpl homeView = new HomeViewImpl();
        homeView.linkPresenter(homePresenter);
        homeView.setView(liveTesterView);

        homeView.startView();
    }

    @Override
    public void stop() throws Exception {
        FxHookWrapper.unregister();
    }
}
