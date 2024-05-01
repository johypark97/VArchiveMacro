package com.github.johypark97.varchivemacro.macro;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultLicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultRecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
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
import java.util.Arrays;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
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
        LOGGER.atError().log("Uncaught Exception", e);
    }

    private static void showUncaughtExceptionAlert(Throwable e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Uncaught Exception");
        alert.setHeaderText("An exception has been thrown and uncaught.");
        alert.setContentText(e.toString());

        StringBuilder builder = new StringBuilder();
        Throwable p = e;
        do {
            String stack = Arrays.stream(p.getStackTrace()).map(x -> "\tat " + x)
                    .collect(Collectors.joining(System.lineSeparator()));

            builder.append(p).append(System.lineSeparator());
            builder.append(stack).append(System.lineSeparator());

            p = p.getCause();
        } while (p != null);

        alert.getDialogPane().setExpandableContent(new TextArea(builder.toString()));
        alert.getDialogPane().setStyle("-fx-font-family: Monospaced; -fx-font-size: 16px;");

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

        homePresenter.linkModel(configModel, databaseModel, recordModel, scannerModel);
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
