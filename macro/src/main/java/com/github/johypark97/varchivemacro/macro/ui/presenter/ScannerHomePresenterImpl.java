package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.UnicodeFilter;
import com.github.johypark97.varchivemacro.macro.common.validator.AccountFileValidator;
import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerHomeViewModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerHomePresenterImpl implements ScannerHome.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerHomePresenterImpl.class);

    private final HomeStage homeStage;

    private final GlobalContext globalContext;

    @MvpView
    public ScannerHome.View view;

    public ScannerHomePresenterImpl(HomeStage homeStage, GlobalContext globalContext) {
        this.homeStage = homeStage;

        this.globalContext = globalContext;
    }

    private boolean validateAccountFile(String value) {
        Language language = Language.INSTANCE;

        Path path;

        try {
            path = PathValidator.validateAndConvert(value);
        } catch (IOException e) {
            homeStage.showError(
                    language.getString("scanner.recordLoader.dialog.accountFile.invalidPath"), e);

            return false;
        }

        try {
            AccountFileValidator.validate(path);
        } catch (NoSuchFileException e) {
            homeStage.showError(
                    language.getString("scanner.recordLoader.dialog.accountFile.notExists"), e);

            return false;
        } catch (InvalidAccountFileException e) {
            homeStage.showError(
                    language.getString("scanner.recordLoader.dialog.accountFile.invalidFile"), e);

            return false;
        } catch (Exception e) {
            String message = "Account file validation exception.";
            LOGGER.atError().setCause(e).log(message);
            homeStage.showError(message, e);

            return false;
        }

        return true;
    }

    private TreeItem<ScannerHomeViewModel.SongTreeViewData> createSongTreeViewData(
            String filterText) {
        UnicodeFilter filter;
        if (filterText.isBlank()) {
            filter = null; // NOPMD
        } else {
            filter = new UnicodeFilter(filterText);
        }

        TreeItem<ScannerHomeViewModel.SongTreeViewData> rootNode = new TreeItem<>();

        globalContext.songService.groupSongByCategory().forEach((category, songList) -> {
            Stream<Song> songStream = songList.stream();

            if (filter != null) {
                songStream = songStream.filter(x -> filter.apply(x.title()));
            }

            List<TreeItem<ScannerHomeViewModel.SongTreeViewData>> songNodeList = songStream.map(
                    x -> new TreeItem<>(ScannerHomeViewModel.SongTreeViewData.from(x))).toList();

            if (!songNodeList.isEmpty()) {
                TreeItem<ScannerHomeViewModel.SongTreeViewData> categoryNode =
                        new TreeItem<>(ScannerHomeViewModel.SongTreeViewData.from(category));

                categoryNode.getChildren().addAll(songNodeList);
                categoryNode.setExpanded(filter != null);

                rootNode.getChildren().add(categoryNode);
            }
        });

        return rootNode;
    }

    private void showRecordViewer() {
        view.setSongTreeViewRoot(createSongTreeViewData(""));
        view.showViewer();
    }

    @Override
    public void startView() {
        view.showProgress("...");

        if (!globalContext.songService.isEmpty() && !globalContext.songRecordService.isEmpty()) {
            showRecordViewer();
            return;
        }

        CompletableFuture.runAsync(() -> {
            Language language = Language.INSTANCE;

            if (globalContext.songService.isEmpty()) {
                Platform.runLater(() -> view.showProgress(
                        language.getString("scanner.recordLoader.progressBox.loadingSong")));

                try {
                    globalContext.songStorageService.load();
                } catch (Exception e) {
                    LOGGER.atError().setCause(e).log("Song loading exception.");
                    Platform.runLater(() -> {
                        homeStage.showError(language.getString(
                                        "scanner.recordLoader.dialog.songLoadingException"), e.toString(),
                                e);
                        view.showUnavailable(
                                language.getString("scanner.recordLoader.progressBox.unavailable"));
                    });

                    return;
                }
            }

            if (globalContext.songRecordService.isEmpty()) {
                Platform.runLater(() -> view.showProgress(
                        language.getString("scanner.recordLoader.progressBox.loadingRecord")));

                try {
                    globalContext.songRecordStorageService.loadFromLocal();
                    Platform.runLater(this::showRecordViewer);

                    return;
                } catch (NoSuchFileException ignored) {
                } catch (Exception e) {
                    LOGGER.atError().setCause(e).log("Local records loading exception.");
                    Platform.runLater(() -> homeStage.showError(
                            language.getString("scanner.recordLoader.dialog.unexpectedError"),
                            e.toString(), e));
                }
            }

            Platform.runLater(this::showRecordLoader);
        });
    }

    @Override
    public boolean stopView() {
        return true;
    }

    @Override
    public void showRecordLoader() {
        view.setAccountFileText(globalContext.configService.findScannerConfig().accountFile());
        view.showLoader();
    }

    @Override
    public void showAccountFileSelector() {
        File accountFile = homeStage.showAccountFileSelector();
        if (accountFile == null) {
            return;
        }

        String pathString =
                new PathHelper(accountFile.toPath()).toRelativeOfOrNot(Path.of("").toAbsolutePath())
                        .toString();
        if (!validateAccountFile(pathString)) {
            return;
        }

        view.setAccountFileText(pathString);
    }

    @Override
    public void loadRemoteRecord() {
        Language language = Language.INSTANCE;

        String djName = view.getDjNameText().trim();
        if (djName.isEmpty()) {
            homeStage.showWarning(language.getString("scanner.recordLoader.dialog.emptyDjName"));
            return;
        }

        String accountFile = view.getAccountFileText().trim();
        if (accountFile.isEmpty()) {
            homeStage.showWarning(
                    language.getString("scanner.recordLoader.dialog.emptyAccountFile"));
            return;
        }

        if (!validateAccountFile(accountFile)) {
            return;
        }

        ScannerConfig.Builder builder = globalContext.configService.findScannerConfig().toBuilder();
        builder.accountFile = accountFile;
        globalContext.configService.saveScannerConfig(builder.build());

        Platform.runLater(() -> view.showProgress(
                language.getString("scanner.recordLoader.progressBox.loadingRecord")));

        CompletableFuture.runAsync(() -> {
            try {
                globalContext.songRecordStorageService.loadFromRemote(djName);
                globalContext.songRecordStorageService.saveToLocal();
            } catch (Exception e) {
                LOGGER.atError().setCause(e).log("Remote records loading exception.");
                Platform.runLater(() -> {
                    homeStage.showError(
                            language.getString("scanner.recordLoader.dialog.remoteError"),
                            e.getMessage(), e);
                    view.showLoader();
                });
                return;
            }

            Platform.runLater(this::showRecordViewer);
        });
    }

    @Override
    public void updateSongFilter(String value) {
        view.setSongTreeViewRoot(createSongTreeViewData(value));
    }

    @Override
    public void showSong(int songId) {
        Song song = globalContext.songService.findSongById(songId);
        view.showSongInformation(song.title(), song.composer());

        SongRecordTable table = globalContext.songRecordService.findById(songId);
        view.showSongRecord(ScannerHomeViewModel.SongRecord.from(table));
    }

    @Override
    public void showScannerWindow() {
        homeStage.showScanner();
    }

    @Override
    public void showHome() {
        homeStage.changeCenterView_modeSelector();
    }
}
