package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.TabHighlightType;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheck.UpdateCheckPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheck.UpdateCheckView;
import com.github.johypark97.varchivemacro.macro.github.GitHubApi;
import com.github.johypark97.varchivemacro.macro.github.VersionChecker;
import com.github.johypark97.varchivemacro.macro.github.data.GitHubRelease;
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

    private final BiConsumer<String, Throwable> showErrorConsumer;
    private final Consumer<TabHighlightType> tabHighlighter;

    @MvpView
    public UpdateCheckView view;

    public UpdateCheckPresenterImpl(BiConsumer<String, Throwable> showError,
            Consumer<TabHighlightType> tabHighlighter) {
        this.showErrorConsumer = showError;
        this.tabHighlighter = tabHighlighter;
    }

    private void checkUpdate_latestRelease(GitHubApi api) throws IOException, InterruptedException {
        VersionChecker versionChecker = new VersionChecker();
        versionChecker.fetch(api);

        String currentVersion = BuildInfo.version;
        if (!versionChecker.isUpdated(currentVersion)) {
            return;
        }

        Platform.runLater(() -> {
            tabHighlighter.accept(TabHighlightType.GREEN);

            GitHubRelease latestRelease = versionChecker.getLatestRelease();
            view.addUpdatedMessage(currentVersion, latestRelease.getVersion(),
                    latestRelease.htmlUrl());
        });
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
                checkUpdate_latestRelease(api);
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
    public void onStartView() {
        checkUpdate();
    }

    @Override
    public void onStopView() {
    }
}
