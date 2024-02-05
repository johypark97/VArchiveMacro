package com.github.johypark97.varchivemacro.dbmanager;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultOcrTestModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultOcrToolModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrTestModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrToolModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.lib.common.FxHookWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");

        launch(args);
    }

    @Override
    public void init() throws Exception {
        FxHookWrapper.disableLogging();
        FxHookWrapper.setEventDispatcher();
        FxHookWrapper.register();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseModel databaseModel = new DefaultDatabaseModel();
        OcrTestModel ocrTestModel = new DefaultOcrTestModel();
        OcrToolModel ocrToolModel = new DefaultOcrToolModel();

        HomePresenterImpl homePresenter = new HomePresenterImpl(HomeViewImpl::new);
        homePresenter.setModel(databaseModel, ocrTestModel, ocrToolModel);

        if (!homePresenter.start()) {
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        FxHookWrapper.unregister();
    }
}
