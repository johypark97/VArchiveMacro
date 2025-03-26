package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.github.DataUpdater;
import com.github.johypark97.varchivemacro.macro.github.GitHubApi;
import com.github.johypark97.varchivemacro.macro.github.VersionChecker;
import com.github.johypark97.varchivemacro.macro.github.data.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Home.TabHighlightType;
import com.github.johypark97.varchivemacro.macro.ui.presenter.UpdateCheck.DataUpdateProgressController;
import com.github.johypark97.varchivemacro.macro.ui.presenter.UpdateCheck.UpdateCheckPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.UpdateCheck.UpdateCheckView;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCheckPresenterImpl implements UpdateCheckPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCheckPresenterImpl.class);

    private final DataUpdater updater = new DataUpdater();

    private final BiConsumer<String, String> showInformation;
    private final BiConsumer<String, Throwable> showErrorConsumer;
    private final Consumer<TabHighlightType> tabHighlighter;

    @MvpView
    public UpdateCheckView view;

    public UpdateCheckPresenterImpl(BiConsumer<String, Throwable> showError,
            BiConsumer<String, String> showInformation, Consumer<TabHighlightType> tabHighlighter) {
        this.showErrorConsumer = showError;
        this.showInformation = showInformation;
        this.tabHighlighter = tabHighlighter;
    }

    private boolean checkUpdate_latestRelease(GitHubApi api)
            throws IOException, InterruptedException {
        VersionChecker versionChecker = new VersionChecker();
        versionChecker.fetch(api);

        String currentVersion = BuildInfo.version;
        if (!versionChecker.isUpdated(currentVersion)) {
            return false;
        }

        Platform.runLater(() -> {
            tabHighlighter.accept(TabHighlightType.GREEN);

            GitHubRelease latestRelease = versionChecker.getLatestRelease();
            view.addProgramUpdatedMessage(currentVersion, latestRelease.getVersion(),
                    latestRelease.htmlUrl());
        });

        return true;
    }

    private boolean checkUpdate_data(GitHubApi api) throws IOException, InterruptedException {
        updater.loadLocalDataVersion();
        updater.fetchRemoteDataVersion(api);

        if (!updater.isUpdated()) {
            return false;
        }

        Platform.runLater(() -> {
            tabHighlighter.accept(TabHighlightType.GREEN);
            view.addDataUpdatedMessage(updater.getCurrentVersion(), updater.getLatestVersion());
        });

        return true;
    }

    private void checkUpdate_showException(Exception e, String message) {
        LOGGER.atError().setCause(e).log(message);

        Platform.runLater(() -> {
            tabHighlighter.accept(TabHighlightType.RED);
            view.addErrorMessage(message, e);
        });
    }

    @Override
    public void showError(String header, Throwable throwable) {
        showErrorConsumer.accept(header, throwable);
    }

    @Override
    public void checkUpdate() {
        view.clearAllMessages();

        CompletableFuture.runAsync(() -> {
            Language language = Language.getInstance();

            Platform.runLater(
                    () -> view.addMessage(language.getString("home.updateCheck.checking")));

            try (GitHubApi api = new GitHubApi()) {
                if (checkUpdate_latestRelease(api) || checkUpdate_data(api)) {
                    return;
                }
            } catch (InterruptedException e) {
                return;
            } catch (IOException e) {
                checkUpdate_showException(e,
                        language.getString("home.updateCheck.error.ioexception"));
                return;
            } catch (Exception e) {
                checkUpdate_showException(e,
                        language.getString("home.updateCheck.error.exception"));
                return;
            }

            Platform.runLater(
                    () -> view.addMessage(language.getString("home.updateCheck.up-to-date")));
        });
    }

    @Override
    public void updateData(Consumer<Boolean> disableUpdateButton) {
        disableUpdateButton.accept(true);

        DataUpdateProgressController controller = view.addDataUpdateProgressMessage();

        BiConsumer<Double, String> progressSetter = (progress, message) -> Platform.runLater(() -> {
            controller.setProgress(progress);
            controller.setMessage(String.format("[%.0f%%] %s", progress * 100, message));
        });

        CompletableFuture.runAsync(() -> {
            progressSetter.accept(-1.0, "");

            Language language = Language.getInstance();

            try (GitHubApi api = new GitHubApi()) {
                updater.downloadRemoteData(api,
                        (maxStep, currentStep, workingFilePath) -> progressSetter.accept(
                                (double) currentStep / maxStep, workingFilePath.toString()));
            } catch (InterruptedException e) {
                return;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                checkUpdate_showException(e,
                        language.getString("home.updateCheck.error.ioexception"));
                return;
            } catch (Exception e) {
                checkUpdate_showException(e,
                        language.getString("home.updateCheck.error.exception"));
                return;
            }

            progressSetter.accept(1.0, "");

            Platform.runLater(() -> {
                String header = language.getString("home.updateCheck.updateComplete.header");
                String message = language.getString("home.updateCheck.updateComplete.message");

                view.addDataUpdateCompleteMessage(header, message);
                showInformation.accept(header, message);
            });
        });
    }

    @Override
    public void onStartView() {
        checkUpdate();
    }

    @Override
    public void onStopView() {
    }
}
