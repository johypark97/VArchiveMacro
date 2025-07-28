package com.github.johypark97.varchivemacro.macro.ui.stage.impl;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.integration.provider.UrlProvider;
import com.github.johypark97.varchivemacro.macro.ui.event.ScannerScanDoneUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEventBus;
import com.github.johypark97.varchivemacro.macro.ui.manager.StageManager;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Home;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Macro;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ModeSelector;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerHome;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.MacroPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ModeSelectorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerHomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.MacroViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ModeSelectorViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerHomeViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.dialog.About;
import io.reactivex.rxjava3.disposables.Disposable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeStageImpl extends AbstractBaseStage implements HomeStage {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeStageImpl.class);

    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final StageManager stageManager;

    private Home.Presenter homePresenter;
    private Macro.Presenter macroPresenter;
    private ModeSelector.Presenter modeSelectorPresenter;
    private ScannerHome.Presenter scannerHomePresenter;

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

    private void onUiEvent(UiEvent uiEvent) {
        if (uiEvent instanceof ScannerScanDoneUiEvent(ScannerContext scannerContext)) {
            stageManager.showScannerProcessorStage(this, scannerContext);
        }
    }

    @Override
    public void startStage() {
        homePresenter = new HomePresenterImpl(this, ContextManager.INSTANCE.getGlobalContext());

        HomeViewImpl view = new HomeViewImpl();
        Mvp.linkViewAndPresenter(view, homePresenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> homePresenter.startView());

        stage.show();

        disposableGlobalEvent = UiEventBus.INSTANCE.subscribe(this::onUiEvent);
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

        modeSelectorPresenter = new ModeSelectorPresenterImpl(this);

        ModeSelectorViewImpl view = new ModeSelectorViewImpl();
        Mvp.linkViewAndPresenter(view, modeSelectorPresenter);

        homePresenter.setCenterView(view);

        modeSelectorPresenter.startView();
    }

    @Override
    public void changeCenterView_freestyleMacro() {
        if (!stopAllCenterView()) {
            return;
        }

        macroPresenter = new MacroPresenterImpl(this, ContextManager.INSTANCE.getGlobalContext(),
                ContextManager.INSTANCE.createMacroContext());

        MacroViewImpl view = new MacroViewImpl();
        Mvp.linkViewAndPresenter(view, macroPresenter);

        homePresenter.setCenterView(view);

        macroPresenter.startView();
    }

    @Override
    public void changeCenterView_collectionScanner() {
        if (!stopAllCenterView()) {
            return;
        }

        scannerHomePresenter =
                new ScannerHomePresenterImpl(this, ContextManager.INSTANCE.getGlobalContext());

        ScannerHomeViewImpl view = new ScannerHomeViewImpl();
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
        try {
            new About(stage, UrlProvider.GITHUB_URL, ContextManager.INSTANCE.getGlobalContext(),
                    ContextManager.INSTANCE.getUpdateCheckContext()).showAndWait();
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
