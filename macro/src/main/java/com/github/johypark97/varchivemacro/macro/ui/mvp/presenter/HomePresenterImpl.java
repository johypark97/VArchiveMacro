package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.app.ProgramVersionService;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.UpdateCheckContext;
import com.github.johypark97.varchivemacro.macro.integration.provider.UrlProvider;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Home;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl implements Home.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private final HomeStage homeStage;

    private final GlobalContext globalContext;
    private final UpdateCheckContext updateCheckContext;

    private final AtomicReference<Throwable> backgroundUpdateCheckException =
            new AtomicReference<>();

    @MvpView
    public Home.View view;

    public HomePresenterImpl(HomeStage homeStage, GlobalContext globalContext,
            UpdateCheckContext updateCheckContext) {
        this.homeStage = homeStage;

        this.globalContext = globalContext;
        this.updateCheckContext = updateCheckContext;
    }

    @Override
    public void startView() {
        Language language = Language.INSTANCE;

        view.setSelectedLanguage(language.getLocale());

        homeStage.changeCenterView_modeSelector();

        try {
            if (!globalContext.configStorageService.load()) {
                globalContext.configStorageService.save();
            }
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Config loading exception.");
            homeStage.showError(language.getString("home.config.loadingException"), e);
        }

        // background update checking
        Single.fromCallable(() -> {
            ProgramVersionService service = updateCheckContext.programVersionService;

            service.fetchLatestRelease();
            if (service.isNewVersionReleased()) {
                return true;
            }

            service.fetchLatestProgramDataVersion();
            return service.isProgramDataUpdated();
        }).subscribeOn(Schedulers.single()).subscribe(updated -> {
            if (updated) {
                Platform.runLater(() -> {
                    view.highlightUpdateCheck(Home.UpdateCheckHightlightColor.GREEN);
                    homeStage.showInformation(
                            language.getString("home.dialog.updateCheck.updated"));
                    showUpdateCheck();
                });
            }
        }, throwable -> {
            backgroundUpdateCheckException.set(throwable);

            LOGGER.atError().setCause(throwable).log("Background update check exception.");
            Platform.runLater(() -> view.highlightUpdateCheck(Home.UpdateCheckHightlightColor.RED));
        });
    }

    @Override
    public boolean stopView() {
        try {
            globalContext.configStorageService.save();
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Config saving exception.");
        }

        return true;
    }

    @Override
    public void requestStopStage() {
        homeStage.stopStage();
    }

    @Override
    public void setCenterView(Node value) {
        view.setCenterNode(value);
    }

    @Override
    public void changeLanguage(Locale locale) {
        Language language = Language.INSTANCE;

        try {
            language.changeLocale(locale);
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Language changing error");
            homeStage.showError(language.getString("home.dialog.languageChange.error"), e);

            return;
        }

        String header = language.getString("home.dialog.languageChange.done.header");
        String content = language.getString("home.dialog.languageChange.done.content");
        homeStage.showInformation(header, content);
    }

    @Override
    public void showSetting() {
        homeStage.showSetting();
    }

    @Override
    public void showOpenSourceLicense() {
        homeStage.showOpenSourceLicense();
    }

    @Override
    public void openManualPage() {
        if (homeStage.showConfirmation(Language.INSTANCE.getString("home.dialog.openManual"))) {
            globalContext.webBrowserService.open(UrlProvider.MANUAL_URL);
        }
    }

    @Override
    public void showAbout() {
        homeStage.showAbout();
    }

    @Override
    public void showUpdateCheck() {
        Optional.ofNullable(backgroundUpdateCheckException.getAndSet(null)).ifPresent(
                x -> homeStage.showError(
                        Language.INSTANCE.getString("home.dialog.updateCheck.exception"), x));

        homeStage.showUpdateCheck();
    }
}
