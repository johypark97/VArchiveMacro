package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.ProgramDataVersionService;
import com.github.johypark97.varchivemacro.macro.common.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.integration.app.service.WebBrowserService;
import com.github.johypark97.varchivemacro.macro.integration.provider.ServiceProvider;
import com.github.johypark97.varchivemacro.macro.integration.provider.UrlProvider;
import com.github.johypark97.varchivemacro.macro.ui.dialog.About;
import com.github.johypark97.varchivemacro.macro.ui.event.GlobalEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.GlobalEventBus;
import com.github.johypark97.varchivemacro.macro.ui.manager.StageManager;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Home;
import com.github.johypark97.varchivemacro.macro.ui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Macro;
import com.github.johypark97.varchivemacro.macro.ui.presenter.MacroPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ModeSelector;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ModeSelectorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerHome;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerHomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.MacroViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.ModeSelectorViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerHomeViewImpl;
import io.reactivex.rxjava3.disposables.Disposable;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeStageImpl extends AbstractTreeableStage implements HomeStage {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeStageImpl.class);

    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final StageManager stageManager;

    private Home.HomePresenter homePresenter;
    private Macro.MacroPresenter macroPresenter;
    private ModeSelector.ModeSelectorPresenter modeSelectorPresenter;
    private ScannerHome.ScannerHomePresenter scannerHomePresenter;

    private Disposable disposableGlobalEvent;

    public HomeStageImpl(StageManager stageManager, Stage stage) {
        super(stage);

        this.stageManager = stageManager;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.setTitle(String.format("%s v%s", TITLE, BuildInfo.version));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);
    }

    private boolean stopAllCenterView() {
        if (modeSelectorPresenter != null) {
            if (!modeSelectorPresenter.stopView()) {
                return false;
            }

            modeSelectorPresenter = null; // NOPMD
        } else if (macroPresenter != null) {
            if (!macroPresenter.stopView()) {
                return false;
            }

            macroPresenter = null; // NOPMD
        } else if (scannerHomePresenter != null) {
            if (!scannerHomePresenter.stopView()) {
                return false;
            }

            scannerHomePresenter = null; // NOPMD
        }

        return true;
    }

    private void onGlobalEvent(GlobalEvent event) {
        if (GlobalEvent.SCANNER_SCAN_DONE.equals(event)) {
            stageManager.showScannerProcessorStage(this);
        }
    }

    @Override
    public void startStage() {
        HomeViewImpl homeView = new HomeViewImpl();

        homePresenter =
                new HomePresenterImpl(this, ServiceProvider.INSTANCE.getConfigStorageService());
        Mvp.linkViewAndPresenter(homeView, homePresenter);

        Scene scene = new Scene(homeView);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> homePresenter.startView());

        stage.show();

        disposableGlobalEvent = GlobalEventBus.INSTANCE.subscribe(this::onGlobalEvent);
    }

    @Override
    public void showError(String content, Throwable throwable) {
        showError(null, content, throwable);
    }

    @Override
    public void showError(String header, String content, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setContentText(content)
                .setThrowable(throwable).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showWarning(String content) {
        Alert alert = AlertBuilder.warning().setOwner(stage).setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void changeCenterView_modeSelector() {
        if (stageManager.isScannerScannerStageOpened()
                || stageManager.isScannerProcessorStageOpened()) {
            return;
        }

        if (!stopAllCenterView()) {
            return;
        }

        ModeSelectorViewImpl view = new ModeSelectorViewImpl();

        modeSelectorPresenter = new ModeSelectorPresenterImpl(this);
        Mvp.linkViewAndPresenter(view, modeSelectorPresenter);

        homePresenter.setCenterView(view);

        modeSelectorPresenter.startView();
    }

    @Override
    public void changeCenterView_freestyleMacro() {
        if (!stopAllCenterView()) {
            return;
        }

        MacroViewImpl view = new MacroViewImpl();

        macroPresenter = new MacroPresenterImpl(this, ServiceProvider.INSTANCE.getConfigService(),
                ServiceProvider.INSTANCE.getMacroService());
        Mvp.linkViewAndPresenter(view, macroPresenter);

        homePresenter.setCenterView(view);

        macroPresenter.startView();
    }

    @Override
    public void changeCenterView_collectionScanner() {
        if (!stopAllCenterView()) {
            return;
        }

        ScannerHomeViewImpl view = new ScannerHomeViewImpl();

        scannerHomePresenter =
                new ScannerHomePresenterImpl(this, ServiceProvider.INSTANCE.getConfigService(),
                        ServiceProvider.INSTANCE.getSongRecordService(),
                        ServiceProvider.INSTANCE.getSongRecordStorageService(),
                        ServiceProvider.INSTANCE.getSongService(),
                        ServiceProvider.INSTANCE.getSongStorageService());
        Mvp.linkViewAndPresenter(view, scannerHomePresenter);

        homePresenter.setCenterView(view);

        scannerHomePresenter.startView();
    }

    @Override
    public File showAccountFileSelector() {
        FileChooser chooser = new FileChooser();

        chooser.setInitialDirectory(Path.of("").toAbsolutePath().toFile());
        chooser.setTitle(
                Language.INSTANCE.getString("scanner.recordLoader.accountFileSelectorTitle"));

        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Account file (*.txt)", "*.txt"));

        return chooser.showOpenDialog(stage);
    }

    @Override
    public void showSetting() {
        stageManager.showSettingStage(this);
    }

    @Override
    public void showOpenSourceLicense() {
        stageManager.showOpenSourceLicenseStage(this);
    }

    @Override
    public void showAbout() {
        ProgramDataVersionService programDataVersionService =
                ServiceProvider.INSTANCE.getProgramDataVersionService();
        WebBrowserService webBrowserService = ServiceProvider.INSTANCE.getWebBrowserService();

        try {
            new About(stage, UrlProvider.GITHUB_URL, programDataVersionService,
                    webBrowserService).showAndWait();
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Opening the About alert exception.");
            showError(Language.INSTANCE.getString("home.about.exception"), e);
        }
    }

    @Override
    public void showScanner() {
        stageManager.showScannerScannerStage(this);
    }

    @Override
    protected boolean onStopStage() {
        if (!stopAllCenterView()) {
            return false;
        }

        disposableGlobalEvent.dispose();

        return homePresenter.stopView();
    }
}
