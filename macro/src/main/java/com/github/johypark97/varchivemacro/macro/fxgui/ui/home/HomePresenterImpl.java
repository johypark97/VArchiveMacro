package com.github.johypark97.varchivemacro.macro.fxgui.ui.home;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultLicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultMacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewerStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer.AnalysisDataViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer.CaptureViewer.CaptureViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer.CaptureViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer.CaptureViewerStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.captureviewer.CaptureViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.MacroPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.MacroViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditor.LinkEditorView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditorStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditorViewImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicensePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicenseStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicenseViewImpl;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private static final Path INITIAL_DIRECTORY = Path.of("").toAbsolutePath();

    private final Function<String, String> VIEWER_TITLE_NORMALIZER =
            x -> Normalizer.normalize(x.toLowerCase(Locale.ENGLISH), Form.NFKD);

    private final NativeKeyListener scannerNativeKeyListener;

    private final ConfigModel configModel;
    private final DatabaseModel databaseModel;
    private final RecordModel recordModel;
    private final ScannerModel scannerModel;

    private MacroViewImpl macroView;

    private AnalysisDataViewerView analysisDataViewerView;
    private CaptureViewerView captureViewerView;

    @MvpView
    public HomeView view;

    public HomePresenterImpl(ConfigModel configModel, DatabaseModel databaseModel,
            RecordModel recordModel, ScannerModel scannerModel) {
        this.configModel = configModel;
        this.databaseModel = databaseModel;
        this.recordModel = recordModel;
        this.scannerModel = scannerModel;

        scannerNativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_BACKSPACE)) {
                    scanner_capture_stop();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isCtrl() && !data.isAlt() && !data.isShift()) {
                    if (data.isPressed(NativeKeyEvent.VC_ENTER)) {
                        scanner_capture_start();
                    }
                }
            }
        };
    }

    private void scanner_viewer_setSongTreeViewRoot(String filter) {
        String normalizedFilter;
        if (filter == null || filter.isBlank()) {
            normalizedFilter = null; // NOPMD
        } else {
            normalizedFilter = VIEWER_TITLE_NORMALIZER.apply(filter.trim());
        }

        TreeItem<ViewerTreeData> rootNode = new TreeItem<>();
        databaseModel.categoryNameSongListMap().forEach((categoryName, songList) -> {
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
            if (!configModel.load()) {
                configModel.save();
            }
        } catch (IOException ignored) {
        }

        macroView = new MacroViewImpl();
        view.setMacroTabContent(macroView);
        Mvp.linkViewAndPresenter(macroView,
                new MacroPresenterImpl(new DefaultMacroModel(), configModel::getMacroConfig,
                        configModel::setMacroConfig, view::showError));
        macroView.startView();

        scannerModel.setupService(throwable -> {
            String header = "Scanner service exception";

            LOGGER.atError().setCause(throwable).log(header);
            view.showError(header, throwable);
        });

        view.scanner_option_setCacheDirectory(
                configModel.getScannerConfig().cacheDirectory.toString());

        view.scanner_option_setupCaptureDelaySlider(ScannerConfig.CAPTURE_DELAY_DEFAULT,
                ScannerConfig.CAPTURE_DELAY_MAX, ScannerConfig.CAPTURE_DELAY_MIN,
                configModel.getScannerConfig().captureDelay);

        view.scanner_option_setupKeyInputDurationSlider(ScannerConfig.KEY_INPUT_DURATION_DEFAULT,
                ScannerConfig.KEY_INPUT_DURATION_MAX, ScannerConfig.KEY_INPUT_DURATION_MIN,
                configModel.getScannerConfig().keyInputDuration);

        view.scanner_option_setupAnalysisThreadCountSlider(
                ScannerConfig.ANALYSIS_THREAD_COUNT_DEFAULT,
                ScannerConfig.ANALYSIS_THREAD_COUNT_MAX,
                configModel.getScannerConfig().analysisThreadCount);

        view.scanner_option_setAccountFile(configModel.getScannerConfig().accountFile.toString());

        view.scanner_option_setupRecordUploadDelaySlider(ScannerConfig.RECORD_UPLOAD_DELAY_DEFAULT,
                ScannerConfig.RECORD_UPLOAD_DELAY_MAX, ScannerConfig.RECORD_UPLOAD_DELAY_MIN,
                configModel.getScannerConfig().recordUploadDelay);



        try {
            databaseModel.load();
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

        view.scanner_capture_setTabList(databaseModel.categoryNameList());
        view.scanner_capture_setSelectedCategorySet(
                configModel.getScannerConfig().selectedCategorySet);

        try {
            if (!recordModel.loadLocal()) {
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
        FxHookWrapper.addKeyListener(scannerNativeKeyListener);
    }

    @Override
    public void onStopView() {
        FxHookWrapper.removeKeyListener(scannerNativeKeyListener);

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
        configModel.setScannerConfig(scannerConfig);

        macroView.stopView();

        try {
            configModel.save();
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
                new OpenSourceLicensePresenterImpl(new DefaultLicenseModel()));

        openSourceLicenseView.startView();
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
            FxHookWrapper.addKeyListener(scannerNativeKeyListener);
        };

        recordModel.loadRemote(djName, onDone, onThrow);
    }

    @Override
    public void scanner_viewer_updateSongTreeViewFilter(String filter) {
        scanner_viewer_setSongTreeViewRoot(filter);
    }

    @Override
    public void scanner_viewer_showRecord(int id) {
        Song song = databaseModel.getSong(id);
        StringBuilder builder = new StringBuilder(32);
        builder.append("Title: ").append(song.title()).append("\nComposer: ")
                .append(song.composer());
        view.scanner_viewer_setSongInformationText(builder.toString());

        ViewerRecordData data = new ViewerRecordData();
        recordModel.getRecordList(id).forEach(x -> {
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
            image = scannerModel.getCaptureImage(cacheDirectoryPath, id);
        } catch (IOException e) {
            view.showError("Cache image reading error", e);
            return;
        }

        if (captureViewerView == null) {
            Stage stage = CaptureViewerStage.create();
            stage.initOwner(view.getWindow());

            captureViewerView = new CaptureViewerViewImpl(stage);
            Mvp.linkViewAndPresenter(captureViewerView, new CaptureViewerPresenterImpl(() -> {
                captureViewerView = null; // NOPMD
            }));
        }

        captureViewerView.startView(SwingFXUtils.toFXImage(image, null));
    }

    @Override
    public void scanner_capture_clearScanData() {
        Runnable onClear = () -> {
            view.scanner_capture_setCaptureDataList(FXCollections.emptyObservableList());
            view.scanner_song_setSongDataList(FXCollections.emptyObservableList());

            scanner_analysis_clearAnalysisData();
        };

        scannerModel.clearScanData(onClear);
    }

    @Override
    public void scanner_capture_start() {
        if (!scannerModel.isScanDataEmpty()) {
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
                    FXCollections.observableArrayList(scannerModel.copyCaptureDataList()));
            view.scanner_song_setSongDataList(
                    FXCollections.observableArrayList(scannerModel.copySongDataList()));

            view.showInformation(header, language.getString("scannerService.dialog.scanCanceled"));
        };
        Runnable onDone = () -> {
            view.scanner_capture_setCaptureDataList(
                    FXCollections.observableArrayList(scannerModel.copyCaptureDataList()));
            view.scanner_song_setSongDataList(
                    FXCollections.observableArrayList(scannerModel.copySongDataList()));

            view.showInformation(header, language.getString("scannerService.dialog.scanDone"));
        };

        Set<String> selectedCategorySet = view.scanner_capture_getSelectedCategorySet();
        int captureDelay = view.scanner_option_getCaptureDelay();
        int keyInputDuration = view.scanner_option_getKeyInputDuration();

        scannerModel.startCollectionScan(onDone, onCancel, databaseModel.categoryNameSongListMap(),
                databaseModel.getTitleTool(), selectedCategorySet, cacheDirectoryPath, captureDelay,
                keyInputDuration);
    }

    @Override
    public void scanner_capture_stop() {
        scannerModel.stopCollectionScan();
    }

    @Override
    public void scanner_song_openLinkEditor(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        Stage stage = LinkEditorStage.create();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(view.getWindow());

        LinkEditorView linkEditorView = new LinkEditorViewImpl(stage);
        Mvp.linkViewAndPresenter(linkEditorView,
                new LinkEditorPresenterImpl(scannerModel, cacheDirectoryPath, id, () -> {
                    view.scanner_capture_refresh();
                    view.scanner_song_refresh();
                }));

        linkEditorView.startView();
    }

    @Override
    public void scanner_analysis_clearAnalysisData() {
        Runnable onClear = () -> {
            view.scanner_analysis_setAnalysisDataList(FXCollections.emptyObservableList());
            view.scanner_uploader_setNewRecordDataList(FXCollections.emptyObservableList());
            view.scanner_analysis_setProgressBarValue(0);
            view.scanner_analysis_setProgressLabelText(null);
        };

        scannerModel.clearAnalysisData(onClear);
    }

    @Override
    public void scanner_analysis_openAnalysisDataViewer(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.scanner_option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        if (analysisDataViewerView == null) {
            Stage stage = AnalysisDataViewerStage.create();
            stage.initOwner(view.getWindow());

            analysisDataViewerView = new AnalysisDataViewerViewImpl(stage);
            Mvp.linkViewAndPresenter(analysisDataViewerView,
                    new AnalysisDataViewerPresenterImpl(scannerModel, () -> {
                        analysisDataViewerView = null; // NOPMD
                    }));
        }

        analysisDataViewerView.startView(cacheDirectoryPath, id);
    }

    @Override
    public void scanner_analysis_startAnalysis() {
        if (scannerModel.isScanDataEmpty() || !scannerModel.isAnalysisDataEmpty()) {
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
                FXCollections.observableArrayList(scannerModel.copyAnalysisDataList()));

        Consumer<Double> onUpdateProgress = value -> {
            view.scanner_analysis_setProgressBarValue(value);
            view.scanner_analysis_setProgressLabelText(String.format("%.2f%%", value * 100));
        };

        int analysisThreadCount = view.scanner_option_getupAnalysisThreadCount();

        scannerModel.starAnalysis(onUpdateProgress, onDataReady, onDone, onCancel,
                cacheDirectoryPath, analysisThreadCount);
    }

    @Override
    public void scanner_analysis_stopAnalysis() {
        scannerModel.stopAnalysis();
    }

    @Override
    public void scanner_uploader_refresh() {
        if (scannerModel.isAnalysisDataEmpty()) {
            return;
        }

        Runnable onDone = () -> view.scanner_uploader_setNewRecordDataList(
                FXCollections.observableArrayList(scannerModel.copyNewRecordDataList()));

        scannerModel.collectNewRecord(onDone, recordModel);
    }

    @Override
    public void scanner_uploader_startUpload(long count) {
        if (scannerModel.isNewRecordDataEmpty()) {
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

        scannerModel.startUpload(onDone, onCancel, databaseModel, recordModel, accountPath,
                recordUploadDelay);
    }

    @Override
    public void scanner_uploader_stopUpload() {
        scannerModel.stopUpload();
    }

    @Override
    public void scanner_option_openCacheDirectorySelector() {
        File file = view.scanner_option_openCacheDirectorySelector(INITIAL_DIRECTORY);
        if (file == null) {
            return;
        }

        Path path = file.toPath();
        try {
            scannerModel.validateCacheDirectory(path);
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
}
