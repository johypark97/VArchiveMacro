package com.github.johypark97.varchivemacro.macro;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultRecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.HomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.HomeStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.HomeViewImpl;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.Toolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Main.logUncaughtException(e));

        System.setProperty("prism.lcdtext", "false");

        ImageConverter.disableDiskCache();

        Language.getInstance().initialize();

        launch(args);
    }

    private static void logUncaughtException(Throwable e) {
        LOGGER.atError().setCause(e).log("Uncaught Exception");
    }

    private static void showUncaughtExceptionAlert(Throwable e) {
        Alert alert = AlertBuilder.error().setTitle("Uncaught Exception")
                .setHeaderText("An exception has been thrown and uncaught.")
                .setContentText(e.toString()).setThrowable(e).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void init() throws Exception {
        FxHookWrapper.disableLogging();
        FxHookWrapper.setEventDispatcher();
        FxHookWrapper.register();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            Main.logUncaughtException(e);
            Main.showUncaughtExceptionAlert(e);
        });

        HomeStage.setupStage(primaryStage);

        HomeView homeView = new HomeViewImpl(primaryStage);
        Mvp.linkViewAndPresenter(homeView,
                new HomePresenterImpl(new DefaultConfigModel(), new DefaultDatabaseModel(),
                        new DefaultRecordModel()));

        Platform.runLater(homeView::startView);
    }

    @Override
    public void stop() throws Exception {
        FxHookWrapper.unregister();
    }
}
