package com.github.johypark97.varchivemacro.dbmanager;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.HomeViewImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseModel databaseModel = new DefaultDatabaseModel();

        HomePresenterImpl homePresenter = new HomePresenterImpl(HomeViewImpl::new);
        homePresenter.setModel(databaseModel);

        if (!homePresenter.start()) {
            Platform.exit();
        }
    }
}
