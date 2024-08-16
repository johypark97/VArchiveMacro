package com.github.johypark97.varchivemacro.macro;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultLicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultMacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultRecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicensePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.AnalysisDataViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.CaptureViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.LinkEditorViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.OpenSourceLicenseViewImpl;
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

    private final ConfigModel configModel = new DefaultConfigModel();
    private final DatabaseModel databaseModel = new DefaultDatabaseModel();
    private final LicenseModel licenseModel = new DefaultLicenseModel();
    private final RecordModel recordModel = new DefaultRecordModel();
    private final ScannerModel scannerModel = new DefaultScannerModel();
    private final MacroModel macroModel = new DefaultMacroModel();

    private final AnalysisDataViewerPresenterImpl analysisDataViewerPresenter =
            new AnalysisDataViewerPresenterImpl();
    private final CaptureViewerPresenterImpl captureViewerPresenter =
            new CaptureViewerPresenterImpl();
    private final HomePresenterImpl homePresenter = new HomePresenterImpl();
    private final LinkEditorPresenterImpl linkViewerPresenter = new LinkEditorPresenterImpl();
    private final OpenSourceLicensePresenterImpl openSourceLicensePresenter =
            new OpenSourceLicensePresenterImpl();

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

        openSourceLicensePresenter.linkModel(licenseModel);
        openSourceLicensePresenter.linkView(new OpenSourceLicenseViewImpl());

        captureViewerPresenter.linkView(new CaptureViewerViewImpl());

        linkViewerPresenter.linkModel(scannerModel);
        linkViewerPresenter.linkView(new LinkEditorViewImpl());

        analysisDataViewerPresenter.linkModel(scannerModel);
        analysisDataViewerPresenter.linkView(new AnalysisDataViewerViewImpl());

        homePresenter.linkModel(configModel, databaseModel, recordModel, scannerModel, macroModel);
        homePresenter.linkPresenter(analysisDataViewerPresenter, captureViewerPresenter,
                linkViewerPresenter, openSourceLicensePresenter);

        homePresenter.linkView(new HomeViewImpl());

        Platform.runLater(homePresenter::startPresenter);
    }

    @Override
    public void stop() throws Exception {
        FxHookWrapper.unregister();
    }
}
