package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.converter.ZonedDateTimeConverter;
import com.github.johypark97.varchivemacro.macro.common.github.domain.Semver;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.app.ProgramVersionService;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.UpdateCheckContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.UpdateCheck;
import com.github.johypark97.varchivemacro.macro.ui.stage.UpdateCheckStage;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCheckPresenterImpl implements UpdateCheck.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCheckPresenterImpl.class);

    private final UpdateCheckStage updateCheckStage;

    private final GlobalContext globalContext;
    private final UpdateCheckContext updateCheckContext;

    private final AtomicBoolean taskRunning = new AtomicBoolean();

    @MvpView
    public UpdateCheck.View view;

    public UpdateCheckPresenterImpl(UpdateCheckStage updateCheckStage, GlobalContext globalContext,
            UpdateCheckContext updateCheckContext) {
        this.updateCheckStage = updateCheckStage;

        this.globalContext = globalContext;
        this.updateCheckContext = updateCheckContext;
    }

    @Override
    public void startView() {
        checkUpdate();
    }

    @Override
    public boolean stopView() {
        return !taskRunning.get();
    }

    @Override
    public void requestStopStage() {
        updateCheckStage.stopStage();
    }

    @Override
    public void showError(String message, Throwable throwable) {
        updateCheckStage.showError(message, throwable.getMessage(), throwable);
    }

    @Override
    public void openBrowser(String url) {
        globalContext.webBrowserService.open(url);
    }

    @Override
    public void checkUpdate() {
        taskRunning.set(true);

        Language language = Language.INSTANCE;

        view.clearAllMessages();
        view.addMessage(language.getString("updateCheck.updateChecking"));

        Single.fromCallable(() -> {
            ProgramVersionService service = updateCheckContext.programVersionService;

            service.fetchLatestRelease();
            if (service.isNewVersionReleased()) {
                String currentVersion = service.getProgramVersion().toString();
                String latestVersion =
                        service.getLatestProgramVersion().map(Semver::toString).orElse("");
                String url = service.getLatestReleaseHtmlUrl().orElse("");

                Platform.runLater(
                        () -> view.addNewVersionReleasedMessage(currentVersion, latestVersion,
                                url));

                return 0;
            }

            service.fetchLatestProgramDataVersion();
            if (service.isProgramDataUpdated()) {
                ZonedDateTime currentVersion = service.getProgramDataVersion();
                ZonedDateTime latestVersion = service.getLatestProgramDataVersion();

                Platform.runLater(() -> view.addProgramDataUpdatedMessage(
                        ZonedDateTimeConverter.format(currentVersion),
                        ZonedDateTimeConverter.format(latestVersion)));

                return 0;
            }

            Platform.runLater(() -> view.addMessage(language.getString("updateCheck.up-to-date")));

            return 0;
        }).subscribeOn(Schedulers.single()).subscribe(x -> taskRunning.set(false), throwable -> {
            taskRunning.set(false);

            LOGGER.atError().setCause(throwable).log("Update checking exception.");
            Platform.runLater(() -> view.addErrorMessage(
                    Language.INSTANCE.getString("updateCheck.exception.checking"), throwable));
        });
    }

    @Override
    public void updateProgramData() {
        taskRunning.set(true);

        UpdateCheck.DataUpdateProgressController controller =
                view.addProgramDataUpdateProgressMessage();

        Single.fromCallable(() -> {
            ProgramVersionService service = updateCheckContext.programVersionService;

            service.updateProgramData(
                    (currentStep, maxStep, workingFilePath) -> Platform.runLater(() -> {
                        double progress = (double) currentStep / maxStep;
                        controller.setProgress(progress);
                        controller.setMessage(String.format("[%.2f%%] %s", progress * 100,
                                workingFilePath.map(Path::toString).orElse("")));
                    }));

            return 0;
        }).subscribeOn(Schedulers.single()).subscribe(x -> {
            taskRunning.set(false);

            Platform.runLater(() -> {
                Language language = Language.INSTANCE;

                view.addMessage(language.getString("updateCheck.updateComplete.message0"),
                        language.getString("updateCheck.updateComplete.message1"),
                        language.getString("updateCheck.updateComplete.message2"));
            });
        }, throwable -> {
            taskRunning.set(false);

            LOGGER.atError().setCause(throwable).log("Program data updating exception.");
            Platform.runLater(() -> view.addErrorMessage(
                    Language.INSTANCE.getString("updateCheck.exception.programDataUpdating"),
                    throwable));
        });
    }
}
