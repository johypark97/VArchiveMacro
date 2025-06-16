package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.SongRecordLoadService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.SongRecordSaveService;
import com.github.johypark97.varchivemacro.macro.application.utility.UnicodeFilter;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.api.loader.AccountFileLoader;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
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

public class ScannerHomePresenterImpl implements ScannerHome.ScannerHomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerHomePresenterImpl.class);

    private final ConfigRepository configRepository;
    private final SongRecordRepository songRecordRepository;
    private final SongRepository songRepository;

    private final SongRecordLoadService songRecordLoadService;
    private final SongRecordSaveService songRecordSaveService;

    private final HomeStage homeStage;

    @MvpView
    public ScannerHome.ScannerHomeView view;

    public ScannerHomePresenterImpl(HomeStage homeStage, ConfigRepository configRepository,
            SongRecordRepository songRecordRepository, SongRepository songRepository,
            SongRecordLoadService songRecordLoadService,
            SongRecordSaveService songRecordSaveService) {
        this.configRepository = configRepository;
        this.songRecordRepository = songRecordRepository;
        this.songRepository = songRepository;

        this.songRecordLoadService = songRecordLoadService;
        this.songRecordSaveService = songRecordSaveService;

        this.homeStage = homeStage;
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
            new AccountFileLoader(path).load();
        } catch (NoSuchFileException e) {
            homeStage.showError(
                    language.getString("scanner.recordLoader.dialog.accountFile.notExists"), e);

            return false;
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Account file validation exception.");

            homeStage.showError(
                    language.getString("scanner.recordLoader.dialog.accountFile.invalidFile"), e);

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

        songRepository.groupSongByCategory().forEach((category, songList) -> {
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
        view.showProgress();

        if (!songRecordRepository.isEmpty()) {
            showRecordViewer();
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                songRepository.load();
                songRecordLoadService.loadFromLocal();
                Platform.runLater(this::showRecordViewer);
                return;
            } catch (NoSuchFileException ignored) {
            } catch (Exception e) {
                LOGGER.atError().setCause(e).log("Local records loading exception.");
                Platform.runLater(() -> homeStage.showError(
                        Language.INSTANCE.getString("scanner.recordLoader.dialog.unexpectedError"),
                        e.toString(), e));
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
        view.setAccountFileText(configRepository.findScannerConfig().accountFile());
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

        ScannerConfig.Builder builder =
                ScannerConfig.Builder.from(configRepository.findScannerConfig());
        builder.accountFile = accountFile;
        configRepository.saveScannerConfig(builder.build());

        Platform.runLater(view::showProgress);

        CompletableFuture.runAsync(() -> {
            try {
                songRecordLoadService.loadFromRemote(djName);
                songRecordSaveService.save();
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
        Song song = songRepository.findSongById(songId);
        view.showSongInformation(song.title(), song.composer());

        SongRecordTable table = songRecordRepository.findById(songId);
        view.showSongRecord(ScannerHomeViewModel.SongRecord.from(table));
    }

    @Override
    public void showScannerWindow() {
    }

    @Override
    public void showHome() {
        homeStage.changeCenterView_modeSelector();
    }
}
