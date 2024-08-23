package com.github.johypark97.varchivemacro.dbmanager;

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
import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
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

    private final HomePresenterImpl homePresenter = new HomePresenterImpl();
    private final LiveTesterPresenterImpl liveTesterPresenter = new LiveTesterPresenterImpl();

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
        } catch (SQLException | IOException | RuntimeException e) {
            LOGGER.atError().setCause(e).log("DatabaseModel exception");
            AlertBuilder.error().setThrowable(e).alert.showAndWait();
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

        liveTesterPresenter.linkModel(databaseModel, liveTesterModel);
        liveTesterPresenter.linkView(new LiveTesterViewImpl());

        homePresenter.linkModel(databaseModel, ocrTestModel, ocrToolModel);
        homePresenter.linkPresenter(liveTesterPresenter);
        homePresenter.linkView(new HomeViewImpl());

        homePresenter.startPresenter();
    }

    @Override
    public void stop() throws Exception {
        FxHookWrapper.unregister();
    }
}
