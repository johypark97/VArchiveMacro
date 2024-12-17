package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck;

import com.github.johypark97.varchivemacro.macro.api.VersionChecker;
import com.github.johypark97.varchivemacro.macro.api.data.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.TabHighlightType;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheck.UpdateCheckPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheck.UpdateCheckView;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCheckPresenterImpl implements UpdateCheckPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateCheckPresenterImpl.class);

    private final BiConsumer<String, Throwable> showError;
    private final Consumer<TabHighlightType> tabHighlighter;

    @MvpView
    public UpdateCheckView view;

    public UpdateCheckPresenterImpl(BiConsumer<String, Throwable> showError,
            Consumer<TabHighlightType> tabHighlighter) {
        this.showError = showError;
        this.tabHighlighter = tabHighlighter;
    }

    private void checkLatestRelease() {
        Language language = Language.getInstance();

        CompletableFuture.runAsync(() -> {
            Platform.runLater(
                    () -> view.addMessage(language.getString("home.updateCheck.checking")));

            VersionChecker versionChecker = new VersionChecker();
            try {
                versionChecker.fetch();
            } catch (InterruptedException e) {
                return;
            } catch (IOException e) {
                String message = language.getString("home.updateCheck.error.ioexception");
                LOGGER.atError().setCause(e).log(message);
                Platform.runLater(() -> {
                    tabHighlighter.accept(TabHighlightType.RED);
                    view.addErrorMessage(message, e);
                });
                return;
            } catch (Exception e) {
                String message = language.getString("home.updateCheck.error.exception");
                LOGGER.atError().setCause(e).log(message);
                Platform.runLater(() -> {
                    tabHighlighter.accept(TabHighlightType.RED);
                    view.addErrorMessage(message, e);
                });
                return;
            }

            String currentVersion = BuildInfo.version;
            if (versionChecker.isUpdated(currentVersion)) {
                GitHubRelease latestRelease = versionChecker.getLatestRelease();
                Platform.runLater(() -> {
                    tabHighlighter.accept(TabHighlightType.GREEN);
                    view.addUpdatedMessage(currentVersion, latestRelease.getVersion(),
                            latestRelease.htmlUrl());
                });
                return;
            }

            Platform.runLater(
                    () -> view.addMessage(language.getString("home.updateCheck.up-to-date")));
        });
    }

    @Override
    public void showError(String header, Throwable throwable) {
        showError.accept(header, throwable);
    }

    @Override
    public void checkAgain() {
        view.clearAllMessages();
        checkLatestRelease();
    }

    @Override
    public void onStartView() {
        checkLatestRelease();
    }

    @Override
    public void onStopView() {
    }
}
