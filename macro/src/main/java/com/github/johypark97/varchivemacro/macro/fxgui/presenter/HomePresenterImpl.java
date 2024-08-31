package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.MacroConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VerticalDirection;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private static final Path INITIAL_DIRECTORY = Path.of("").toAbsolutePath();

    private final Function<String, String> VIEWER_TITLE_NORMALIZER =
            x -> Normalizer.normalize(x.toLowerCase(Locale.ENGLISH), Form.NFKD);

    private WeakReference<ConfigModel> configModelReference;
    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<RecordModel> recordModelReference;
    private WeakReference<ScannerModel> scannerModelReference;
    private WeakReference<MacroModel> macroModelReference;

    @MvpView
    public HomeView view;

    public void linkModel(ConfigModel configModel, DatabaseModel databaseModel,
            RecordModel recordModel, ScannerModel scannerModel, MacroModel macroModel) {
        configModelReference = new WeakReference<>(configModel);
        databaseModelReference = new WeakReference<>(databaseModel);
        recordModelReference = new WeakReference<>(recordModel);
        scannerModelReference = new WeakReference<>(scannerModel);
        macroModelReference = new WeakReference<>(macroModel);
    }

    private ConfigModel getConfigModel() {
        return configModelReference.get();
    }

    private DatabaseModel getDatabaseModel() {
        return databaseModelReference.get();
    }

    private RecordModel getRecordModel() {
        return recordModelReference.get();
    }

    private ScannerModel getScannerModel() {
        return scannerModelReference.get();
    }

    private MacroModel getMacroModel() {
        return macroModelReference.get();
    }

    private void scanner_viewer_setSongTreeViewRoot(String filter) {
        String normalizedFilter;
        if (filter == null || filter.isBlank()) {
            normalizedFilter = null; // NOPMD
        } else {
            normalizedFilter = VIEWER_TITLE_NORMALIZER.apply(filter.trim());
        }

        TreeItem<ViewerTreeData> rootNode = new TreeItem<>();
        getDatabaseModel().categoryNameSongListMap().forEach((categoryName, songList) -> {
            TreeItem<ViewerTreeData> categoryNode =
                    new TreeItem<>(new ViewerTreeData(categoryName));
            categoryNode.setExpanded(normalizedFilter != null);
            rootNode.getChildren().add(categoryNode);

            Stream<Song> stream = songList.stream();
            if (normalizedFilter != null) {
                stream = stream.filter(
                        x -> VIEWER_TITLE_NORMALIZER.apply(x.title()).contains(normalizedFilter));
            }
            stream.forEach(song -> categoryNode.getChildren()
                    .add(new TreeItem<>(new ViewerTreeData(song))));
        });

        view.scanner_viewer_setSongTreeViewRoot(rootNode);
    }

    private Path convertStringToPath(String pathString) {
        try {
            return Path.of(pathString);
        } catch (InvalidPathException e) {
            view.showError("Invalid path.", e);
            return null;
        }
    }

    @Override
    public void onStartView() {
        try {
            if (!getConfigModel().load()) {
                getConfigModel().save();
            }
        } catch (IOException ignored) {
        }

        getScannerModel().setupService(throwable -> {
            String header = "Scanner service exception";

            LOGGER.atError().setCause(throwable).log(header);
            view.showError(header, throwable);
        });

        getMacroModel().setupService(throwable -> {
            String header = "Macro service exception";

            LOGGER.atError().setCause(throwable).log(header);
            view.showError(header, throwable);
        });

        view.scanner_option_setCacheDirectory(
                getConfigModel().getScannerConfig().cacheDirectory.toString());

        view.scanner_option_setupCaptureDelaySlider(ScannerConfig.CAPTURE_DELAY_DEFAULT,
                ScannerConfig.CAPTURE_DELAY_MAX, ScannerConfig.CAPTURE_DELAY_MIN,
                getConfigModel().getScannerConfig().captureDelay);

        view.scanner_option_setupKeyInputDurationSlider(ScannerConfig.KEY_INPUT_DURATION_DEFAULT,
                ScannerConfig.KEY_INPUT_DURATION_MAX, ScannerConfig.KEY_INPUT_DURATION_MIN,
                getConfigModel().getScannerConfig().keyInputDuration);

        view.scanner_option_setupAnalysisThreadCountSlider(
                ScannerConfig.ANALYSIS_THREAD_COUNT_DEFAULT,
                ScannerConfig.ANALYSIS_THREAD_COUNT_MAX,
                getConfigModel().getScannerConfig().analysisThreadCount);

        view.scanner_option_setAccountFile(
                getConfigModel().getScannerConfig().accountFile.toString());

        view.scanner_option_setupRecordUploadDelaySlider(ScannerConfig.RECORD_UPLOAD_DELAY_DEFAULT,
                ScannerConfig.RECORD_UPLOAD_DELAY_MAX, ScannerConfig.RECORD_UPLOAD_DELAY_MIN,
                getConfigModel().getScannerConfig().recordUploadDelay);

        view.macro_setAnalysisKey(getConfigModel().getMacroConfig().analysisKey);

        view.macro_setupCountSlider(MacroConfig.COUNT_DEFAULT, MacroConfig.COUNT_MAX,
                MacroConfig.COUNT_MIN, getConfigModel().getMacroConfig().count);

        view.macro_setupCaptureDelaySlider(MacroConfig.CAPTURE_DELAY_DEFAULT,
                MacroConfig.CAPTURE_DELAY_MAX, MacroConfig.CAPTURE_DELAY_MIN,
                getConfigModel().getMacroConfig().captureDelay);

        view.macro_setupCaptureDurationSlider(MacroConfig.CAPTURE_DURATION_DEFAULT,
                MacroConfig.CAPTURE_DURATION_MAX, MacroConfig.CAPTURE_DURATION_MIN,
                getConfigModel().getMacroConfig().captureDuration);

        view.macro_setupKeyInputDurationSlider(MacroConfig.KEY_INPUT_DURATION_DEFAULT,
                MacroConfig.KEY_INPUT_DURATION_MAX, MacroConfig.KEY_INPUT_DURATION_MIN,
                getConfigModel().getMacroConfig().keyInputDuration);

        try {
            getDatabaseModel().load();
        } catch (SQLException | IOException e) {
            view.getScannerFrontController().showForbiddenMark();
            view.showError("Database loading error", e);
            LOGGER.atError().setCause(e).log("DatabaseModel loading exception");
            return;
        } catch (Exception e) {
            view.getScannerFrontController().showForbiddenMark();
            view.showError("Critical database loading error", e);
            LOGGER.atError().setCause(e).log("Critical DatabaseModel loading exception");
            throw e;
        }

        scanner_viewer_setSongTreeViewRoot(null);

        view.scanner_capture_setTabList(getDatabaseModel().categoryNameList());
        view.scanner_capture_setSelectedCategorySet(
                getConfigModel().getScannerConfig().selectedCategorySet);

        try {
            if (!getRecordModel().loadLocal()) {
                view.getScannerFrontController().showDjNameInput();
                return;
            }
        } catch (IOException e) {
            view.getScannerFrontController().showDjNameInput();
            view.showError("Local records loading error", e);
            LOGGER.atError().setCause(e).log("RecordModel loading exception");
            return;
        } catch (Exception e) {
            view.getScannerFrontController().showDjNameInput();
            view.showError("Critical local records loading error", e);
            LOGGER.atError().setCause(e).log("Critical RecordModel loading exception");
            throw e;
        }

        view.getScannerFrontController().showScanner();
    }

    @Override
    public void onStopView() {
        ScannerConfig scannerConfig = new ScannerConfig();
        {
            scannerConfig.selectedCategorySet = view.scanner_capture_getSelectedCategorySet();

            try {
                scannerConfig.cacheDirectory = Path.of(view.scanner_option_getCacheDirectory());
            } catch (InvalidPathException ignored) {
            }

            scannerConfig.captureDelay = view.scanner_option_getCaptureDelay();
            scannerConfig.keyInputDuration = view.scanner_option_getKeyInputDuration();

            scannerConfig.analysisThreadCount = view.scanner_option_getupAnalysisThreadCount();

            try {
                scannerConfig.accountFile = Path.of(view.scanner_option_getAccountFile());
            } catch (InvalidPathException ignored) {
            }

            scannerConfig.recordUploadDelay = view.scanner_option_getRecordUploadDelay();
        }
        getConfigModel().setScannerConfig(scannerConfig);

        MacroConfig macroConfig = new MacroConfig();
        {
            macroConfig.analysisKey = view.macro_getAnalysisKey();
            macroConfig.count = view.macro_getCount();
            macroConfig.captureDelay = view.macro_getCaptureDelay();
            macroConfig.captureDuration = view.macro_getCaptureDuration();
            macroConfig.keyInputDuration = view.macro_getKeyInputDuration();
        }
        getConfigModel().setMacroConfig(macroConfig);

        try {
            getConfigModel().save();
        } catch (IOException ignored) {
        }
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
        view.home_openOpenSourceLicense();
    }

    @Override
    public void home_openAbout() {
        view.home_openAbout();
    }

    @Override
    public void scanner_front_loadRemoteRecord(String djName) {
        view.getScannerFrontController().hideDjNameInputError();

        if (djName.isBlank()) {
            String message = Language.getInstance().getString("scannerDjNameInput.blankError");
            view.getScannerFrontController().showDjNameInputError(message);
            return;
        }

        view.getScannerFrontController().showLoadingMark(djName);

        BiConsumer<String, Exception> onThrow = view::showError;
        Consumer<Boolean> onDone = x -> {
            view.getScannerFrontController().hideLoadingMark();

            if (Boolean.FALSE.equals(x)) {
                view.getScannerFrontController().showDjNameInput();
                return;
            }

            view.getScannerFrontController().showScanner();
        };

        getRecordModel().loadRemote(djName, onDone, onThrow);
    }

    @Override
    public void scanner_viewer_updateSongTreeViewFilter(String filter) {
        scanner_viewer_setSongTreeViewRoot(filter);
    }

    @Override
    public void scanner_viewer_showRecord(int id) {
        Song song = getDatabaseModel().getSong(id);
        StringBuilder builder = new StringBuilder(32);
        builder.append("Title: ").append(song.title()).append("\nComposer: ")
                .append(song.composer());
        view.scanner_viewer_setSongInformationText(builder.toString());

        ViewerRecordData data = new ViewerRecordData();
        getRecordModel().getRecordList(id).forEach(x -> {
            int column = x.pattern.getWeight();
            int row = x.button.getWeight();

            data.maxCombo[row][column] = x.maxCombo;
            data.rate[row][column] = x.rate;
        });
        view.scanner_viewer_setRecordData(data);
    }

    @Override
    public void scanner_capture_openCaptureViewer(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        BufferedImage image;
        try {
            image = getScannerModel().getCaptureImage(cacheDirectoryPath, id);
        } catch (IOException e) {
            view.showError("Cache image reading error", e);
            return;
        }

        view.scanner_capture_openCaptureViewer(SwingFXUtils.toFXImage(image, null));
    }

    @Override
    public void scanner_capture_clearScanData() {
        Runnable onClear = () -> {
            view.scanner_capture_setCaptureDataList(FXCollections.emptyObservableList());
            view.scanner_song_setSongDataList(FXCollections.emptyObservableList());

            scanner_analysis_clearAnalysisData();
        };

        getScannerModel().clearScanData(onClear);
    }

    @Override
    public void scanner_capture_start() {
        if (!getScannerModel().isScanDataEmpty()) {
            return;
        }

        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerService.dialog.header");

        Runnable onCancel = () -> {
            view.scanner_capture_setCaptureDataList(
                    FXCollections.observableArrayList(getScannerModel().copyCaptureDataList()));
            view.scanner_song_setSongDataList(
                    FXCollections.observableArrayList(getScannerModel().copySongDataList()));

            view.showInformation(header, language.getString("scannerService.dialog.scanCanceled"));
        };
        Runnable onDone = () -> {
            view.scanner_capture_setCaptureDataList(
                    FXCollections.observableArrayList(getScannerModel().copyCaptureDataList()));
            view.scanner_song_setSongDataList(
                    FXCollections.observableArrayList(getScannerModel().copySongDataList()));

            view.showInformation(header, language.getString("scannerService.dialog.scanDone"));
        };

        Set<String> selectedCategorySet = view.scanner_capture_getSelectedCategorySet();
        int captureDelay = view.scanner_option_getCaptureDelay();
        int keyInputDuration = view.scanner_option_getKeyInputDuration();

        getScannerModel().startCollectionScan(onDone, onCancel,
                getDatabaseModel().categoryNameSongListMap(), getDatabaseModel().getTitleTool(),
                selectedCategorySet, cacheDirectoryPath, captureDelay, keyInputDuration);
    }

    @Override
    public void scanner_capture_stop() {
        getScannerModel().stopCollectionScan();
    }

    @Override
    public void scanner_song_openLinkEditor(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        view.scanner_song_openLinkEditor(cacheDirectoryPath, id, () -> {
            view.scanner_capture_refresh();
            view.scanner_song_refresh();
        });
    }

    @Override
    public void scanner_analysis_clearAnalysisData() {
        Runnable onClear = () -> {
            view.scanner_analysis_setAnalysisDataList(FXCollections.emptyObservableList());
            view.scanner_uploader_setNewRecordDataList(FXCollections.emptyObservableList());
            view.scanner_analysis_setProgressBarValue(0);
            view.scanner_analysis_setProgressLabelText(null);
        };

        getScannerModel().clearAnalysisData(onClear);
    }

    @Override
    public void scanner_analysis_openAnalysisDataViewer(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        view.scanner_analysis_openAnalysisDataViewer(cacheDirectoryPath, id);
    }

    @Override
    public void scanner_analysis_startAnalysis() {
        if (getScannerModel().isScanDataEmpty() || !getScannerModel().isAnalysisDataEmpty()) {
            return;
        }

        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerService.dialog.header");

        Runnable onCancel = () -> {
            scanner_uploader_refresh();
            view.showInformation(header,
                    language.getString("scannerService.dialog.analysisCanceled"));
        };
        Runnable onDone = () -> {
            scanner_uploader_refresh();
            view.showInformation(header, language.getString("scannerService.dialog.analysisDone"));
        };

        Runnable onDataReady = () -> view.scanner_analysis_setAnalysisDataList(
                FXCollections.observableArrayList(getScannerModel().copyAnalysisDataList()));

        Consumer<Double> onUpdateProgress = value -> {
            view.scanner_analysis_setProgressBarValue(value);
            view.scanner_analysis_setProgressLabelText(String.format("%.2f%%", value * 100));
        };

        int analysisThreadCount = view.scanner_option_getupAnalysisThreadCount();

        getScannerModel().starAnalysis(onUpdateProgress, onDataReady, onDone, onCancel,
                cacheDirectoryPath, analysisThreadCount);
    }

    @Override
    public void scanner_analysis_stopAnalysis() {
        getScannerModel().stopAnalysis();
    }

    @Override
    public void scanner_uploader_refresh() {
        if (getScannerModel().isAnalysisDataEmpty()) {
            return;
        }

        Runnable onDone = () -> view.scanner_uploader_setNewRecordDataList(
                FXCollections.observableArrayList(getScannerModel().copyNewRecordDataList()));

        getScannerModel().collectNewRecord(onDone, getRecordModel());
    }

    @Override
    public void scanner_uploader_startUpload(long count) {
        if (getScannerModel().isNewRecordDataEmpty()) {
            return;
        }

        Language language = Language.getInstance();

        {
            String header = language.getString("scanner.uploader.dialog.header");
            String content = language.getFormatString("scanner.uploader.dialog.content", count);

            if (!view.showConfirmation(header, content)) {
                return;
            }
        }

        Path accountPath = convertStringToPath(view.scanner_option_getAccountFile());
        if (accountPath == null) {
            return;
        }

        int recordUploadDelay = view.scanner_option_getRecordUploadDelay();

        Runnable onCancel;
        Runnable onDone;
        {
            String header = language.getString("scannerService.dialog.header");

            onCancel = () -> view.showInformation(header,
                    language.getString("scannerService.dialog.uploadCanceled"));
            onDone = () -> view.showInformation(header,
                    language.getString("scannerService.dialog.uploadDone"));
        }

        getScannerModel().startUpload(onDone, onCancel, getDatabaseModel(), getRecordModel(),
                accountPath, recordUploadDelay);
    }

    @Override
    public void scanner_uploader_stopUpload() {
        getScannerModel().stopUpload();
    }

    @Override
    public void scanner_option_openCacheDirectorySelector() {
        File file = view.scanner_option_openCacheDirectorySelector(INITIAL_DIRECTORY);
        if (file == null) {
            return;
        }

        Path path = file.toPath();
        try {
            getScannerModel().validateCacheDirectory(path);
        } catch (IOException e) {
            String header =
                    Language.getInstance().getString("scanner.option.dialog.invalidDirectory");
            view.showError(header, e);

            return;
        }

        path = new PathHelper(path).toRelativeOfOrNot(INITIAL_DIRECTORY);
        if (path != null) {
            view.scanner_option_setCacheDirectory(path.toString());
        }
    }

    @Override
    public void scanner_option_openAccountFileSelector() {
        File file = view.scanner_option_openAccountFileSelector(INITIAL_DIRECTORY);
        if (file == null) {
            return;
        }

        Path path = new PathHelper(file.toPath()).toRelativeOfOrNot(INITIAL_DIRECTORY);
        if (path != null) {
            view.scanner_option_setAccountFile(path.toString());
        }
    }

    @Override
    public void macro_start_up() {
        AnalysisKey analysisKey = view.macro_getAnalysisKey();
        int count = view.macro_getCount();
        int captureDelay = view.macro_getCaptureDelay();
        int captureDuration = view.macro_getCaptureDuration();
        int keyInputDuration = view.macro_getKeyInputDuration();

        getMacroModel().startMacro(analysisKey, count, captureDelay, captureDuration,
                keyInputDuration, VerticalDirection.UP);
    }

    @Override
    public void macro_start_down() {
        AnalysisKey analysisKey = view.macro_getAnalysisKey();
        int count = view.macro_getCount();
        int captureDelay = view.macro_getCaptureDelay();
        int captureDuration = view.macro_getCaptureDuration();
        int keyInputDuration = view.macro_getKeyInputDuration();

        getMacroModel().startMacro(analysisKey, count, captureDelay, captureDuration,
                keyInputDuration, VerticalDirection.DOWN);
    }

    @Override
    public void macro_stop() {
        getMacroModel().stopMacro();
    }
}
