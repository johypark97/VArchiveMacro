package com.github.johypark97.varchivemacro.macro.fxgui.ui.home;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.MacroPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.MacroViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner.ScannerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner.ScannerViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.ScannerLoaderPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.ScannerLoaderViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheckPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheckViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicensePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicenseStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicenseViewImpl;
import com.github.johypark97.varchivemacro.macro.github.DataUpdater;
import com.github.johypark97.varchivemacro.macro.provider.RepositoryProvider;
import com.github.johypark97.varchivemacro.macro.provider.ServiceProvider;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.io.IOException;
import java.util.Locale;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private final RepositoryProvider repositoryProvider;
    private final ServiceProvider serviceProvider;

    private MacroViewImpl macroView;
    private ScannerViewImpl scannerView;

    @MvpView
    public HomeView view;

    public HomePresenterImpl(RepositoryProvider repositoryProvider,
            ServiceProvider serviceProvider) {
        this.repositoryProvider = repositoryProvider;
        this.serviceProvider = serviceProvider;
    }

    private void showScanner() {
        scannerView = new ScannerViewImpl();
        view.setScannerTabContent(scannerView);
        Mvp.linkViewAndPresenter(scannerView,
                new ScannerPresenterImpl(repositoryProvider, serviceProvider, view::showInformation,
                        view::showError, view::showConfirmation, view::getWindow));

        scannerView.startView();
    }

    @Override
    public void onStartView() {
        ConfigRepository configRepository = repositoryProvider.getConfigRepository();

        try {
            if (!configRepository.load()) {
                configRepository.save();
            }
        } catch (IOException ignored) {
        }

        ScannerLoaderViewImpl scannerLoaderView = new ScannerLoaderViewImpl();
        view.setScannerTabContent(scannerLoaderView);
        Mvp.linkViewAndPresenter(scannerLoaderView,
                new ScannerLoaderPresenterImpl(repositoryProvider, view::showError,
                        this::showScanner));

        macroView = new MacroViewImpl();
        view.setMacroTabContent(macroView);
        Mvp.linkViewAndPresenter(macroView,
                new MacroPresenterImpl(repositoryProvider, serviceProvider, view::showError));

        UpdateCheckViewImpl updateCheckView = new UpdateCheckViewImpl();
        view.setUpdateCheckTabContent(updateCheckView);
        Mvp.linkViewAndPresenter(updateCheckView,
                new UpdateCheckPresenterImpl(view::showError, view::showInformation,
                        view::highlightUpdateCheckTab));

        Platform.runLater(scannerLoaderView::startView);
        Platform.runLater(macroView::startView);
        Platform.runLater(updateCheckView::startView);
    }

    @Override
    public void onStopView() {
        ConfigRepository configRepository = repositoryProvider.getConfigRepository();

        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        if (scannerView != null) {
            scannerView.stopView();
        }

        macroView.stopView();

        try {
            configRepository.save();
        } catch (IOException ignored) {
        }

        view.getWindow().hide();
    }

    @Override
    public void home_changeLanguage(Locale locale) {
        try {
            Language.saveLocale(locale);
        } catch (IOException e) {
            view.showError("Language changing error", e);
            LOGGER.atError().setCause(e).log("Language changing error");
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("home.dialog.languageChange.header");
        String content = language.getString("home.dialog.languageChange.content");
        view.showInformation(header, content);
    }

    @Override
    public void home_openOpenSourceLicense() {
        Stage stage = OpenSourceLicenseStage.create();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(view.getWindow());

        OpenSourceLicenseView openSourceLicenseView = new OpenSourceLicenseViewImpl(stage);
        Mvp.linkViewAndPresenter(openSourceLicenseView,
                new OpenSourceLicensePresenterImpl(repositoryProvider));

        openSourceLicenseView.startView();
    }

    @Override
    public void home_openAbout() {
        long dataVersion = -1;

        try {
            DataUpdater updater = new DataUpdater();
            updater.loadLocalDataVersion();
            dataVersion = updater.getCurrentVersion();
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log();
        }

        view.home_openAbout(BuildInfo.date, BuildInfo.version, String.valueOf(dataVersion));
    }
}
