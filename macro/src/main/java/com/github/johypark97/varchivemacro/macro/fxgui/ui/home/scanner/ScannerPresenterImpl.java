package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.ScannerConfig;
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
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner.Scanner.ScannerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner.Scanner.ScannerView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner.Scanner.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner.Scanner.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditor;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditorStage;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor.LinkEditorViewImpl;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerPresenterImpl implements ScannerPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerPresenterImpl.class);

    private static final Path INITIAL_DIRECTORY = Path.of("").toAbsolutePath();

    private final Function<String, String> VIEWER_TITLE_NORMALIZER =
            x -> Normalizer.normalize(x.toLowerCase(Locale.ENGLISH), Normalizer.Form.NFKD);

    private final NativeKeyListener scannerNativeKeyListener;

    private final DatabaseRepository databaseRepository;
    private final RecordModel recordModel;
    private final ScannerModel scannerModel;

    private final BiConsumer<String, String> showInformation;
    private final BiConsumer<String, Throwable> showError;
    private final BiPredicate<String, String> showConfirmation;
    private final Consumer<ScannerConfig> scannerConfigSetter;
    private final Supplier<ScannerConfig> scannerConfigGetter;
    private final Supplier<Window> windowSupplier;

    private AnalysisDataViewerView analysisDataViewerView;
    private CaptureViewerView captureViewerView;

    @MvpView
    public ScannerView view;

    public ScannerPresenterImpl(DatabaseRepository databaseRepository, RecordModel recordModel,
            ScannerModel scannerModel, BiConsumer<String, String> showInformation,
            BiConsumer<String, Throwable> showError, BiPredicate<String, String> showConfirmation,
            Supplier<ScannerConfig> scannerConfigGetter,
            Consumer<ScannerConfig> scannerConfigSetter, Supplier<Window> windowSupplier) {
        this.databaseRepository = databaseRepository;
        this.recordModel = recordModel;
        this.scannerConfigGetter = scannerConfigGetter;
        this.scannerConfigSetter = scannerConfigSetter;
        this.scannerModel = scannerModel;
        this.showConfirmation = showConfirmation;
        this.showError = showError;
        this.showInformation = showInformation;
        this.windowSupplier = windowSupplier;

        scannerNativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_BACKSPACE)) {
                    capture_stop();
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
                        capture_start();
                    }
                }
            }
        };
    }

    private void viewer_setSongTreeViewRoot(String filter) {
        String normalizedFilter;
        if (filter == null || filter.isBlank()) {
            normalizedFilter = null; // NOPMD
        } else {
            normalizedFilter = VIEWER_TITLE_NORMALIZER.apply(filter.trim());
        }

        TreeItem<ViewerTreeData> rootNode = new TreeItem<>();
        databaseRepository.categoryNameSongListMap().forEach((categoryName, songList) -> {
            TreeItem<ViewerTreeData> categoryNode =
                    new TreeItem<>(new ViewerTreeData(categoryName));
            categoryNode.setExpanded(normalizedFilter != null);
            rootNode.getChildren().add(categoryNode);

            Stream<SongDatabase.Song> stream = songList.stream();
            if (normalizedFilter != null) {
                stream = stream.filter(
                        x -> VIEWER_TITLE_NORMALIZER.apply(x.title()).contains(normalizedFilter));
            }
            stream.forEach(song -> categoryNode.getChildren()
                    .add(new TreeItem<>(new ViewerTreeData(song))));
        });

        view.viewer_setSongTreeViewRoot(rootNode);
    }

    private Path convertStringToPath(String pathString) {
        try {
            return Path.of(pathString);
        } catch (InvalidPathException e) {
            showError.accept("Invalid path.", e);
            return null;
        }
    }

    private File openCacheDirectorySelector() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        chooser.setTitle(Language.getInstance()
                .getString("scanner.option.dialog.cacheDirectorySelectorTitle"));

        return chooser.showDialog(windowSupplier.get());
    }

    private File openAccountFileSelector() {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        chooser.setTitle(
                Language.getInstance().getString("scanner.option.dialog.AccountFileSelectorTitle"));

        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Account text file (*.txt)", "*.txt"));

        return chooser.showOpenDialog(windowSupplier.get());
    }

    @Override
    public void onStartView() {
        scannerModel.setupService(throwable -> {
            String header = "Scanner service exception";

            LOGGER.atError().setCause(throwable).log(header);
            showError.accept(header, throwable);
        });

        ScannerConfig scannerConfig = scannerConfigGetter.get();

        view.option_setCacheDirectory(scannerConfig.cacheDirectory.toString());

        view.option_setupCaptureDelaySlider(ScannerConfig.CAPTURE_DELAY_DEFAULT,
                ScannerConfig.CAPTURE_DELAY_MAX, ScannerConfig.CAPTURE_DELAY_MIN,
                scannerConfig.captureDelay);

        view.option_setupKeyInputDurationSlider(ScannerConfig.KEY_INPUT_DURATION_DEFAULT,
                ScannerConfig.KEY_INPUT_DURATION_MAX, ScannerConfig.KEY_INPUT_DURATION_MIN,
                scannerConfig.keyInputDuration);

        view.option_setupAnalysisThreadCountSlider(ScannerConfig.ANALYSIS_THREAD_COUNT_DEFAULT,
                ScannerConfig.ANALYSIS_THREAD_COUNT_MAX, scannerConfig.analysisThreadCount);

        view.option_setAccountFile(scannerConfig.accountFile.toString());

        view.option_setupRecordUploadDelaySlider(ScannerConfig.RECORD_UPLOAD_DELAY_DEFAULT,
                ScannerConfig.RECORD_UPLOAD_DELAY_MAX, ScannerConfig.RECORD_UPLOAD_DELAY_MIN,
                scannerConfig.recordUploadDelay);

        viewer_setSongTreeViewRoot(null);

        view.capture_setTabList(databaseRepository.categoryNameList());
        view.capture_setSelectedCategorySet(scannerConfig.selectedCategorySet);

        FxHookWrapper.addKeyListener(scannerNativeKeyListener);
    }

    @Override
    public void onStopView() {
        FxHookWrapper.removeKeyListener(scannerNativeKeyListener);

        ScannerConfig scannerConfig = new ScannerConfig();
        {
            scannerConfig.selectedCategorySet = view.capture_getSelectedCategorySet();

            try {
                scannerConfig.cacheDirectory = Path.of(view.option_getCacheDirectory());
            } catch (InvalidPathException ignored) {
            }

            scannerConfig.captureDelay = view.option_getCaptureDelay();
            scannerConfig.keyInputDuration = view.option_getKeyInputDuration();

            scannerConfig.analysisThreadCount = view.option_getAnalysisThreadCount();

            try {
                scannerConfig.accountFile = Path.of(view.option_getAccountFile());
            } catch (InvalidPathException ignored) {
            }

            scannerConfig.recordUploadDelay = view.option_getRecordUploadDelay();
        }

        scannerConfigSetter.accept(scannerConfig);
    }

    @Override
    public void viewer_updateSongTreeViewFilter(String filter) {
        viewer_setSongTreeViewRoot(filter);
    }

    @Override
    public void viewer_showRecord(int id) {
        SongDatabase.Song song = databaseRepository.getSong(id);
        StringBuilder builder = new StringBuilder(32);
        builder.append("Title: ").append(song.title()).append("\nComposer: ")
                .append(song.composer());
        view.viewer_setSongInformationText(builder.toString());

        ViewerRecordData data = new ViewerRecordData();
        recordModel.getRecordList(id).forEach(x -> {
            int column = x.pattern.getWeight();
            int row = x.button.getWeight();

            data.maxCombo[row][column] = x.maxCombo;
            data.rate[row][column] = x.rate;
        });
        view.viewer_setRecordData(data);
    }

    @Override
    public void capture_openCaptureViewer(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        BufferedImage image;
        try {
            image = scannerModel.getCaptureImage(cacheDirectoryPath, id);
        } catch (IOException e) {
            showError.accept("Cache image reading error", e);
            return;
        }

        if (captureViewerView == null) {
            Stage stage = CaptureViewerStage.create();
            stage.initOwner(windowSupplier.get());

            captureViewerView = new CaptureViewerViewImpl(stage);
            Mvp.linkViewAndPresenter(captureViewerView, new CaptureViewerPresenterImpl(() -> {
                captureViewerView = null; // NOPMD
            }));
        }

        captureViewerView.startView(SwingFXUtils.toFXImage(image, null));
    }

    @Override
    public void capture_clearScanData() {
        Runnable onClear = () -> {
            view.capture_setCaptureDataList(FXCollections.emptyObservableList());
            view.song_setSongDataList(FXCollections.emptyObservableList());

            analysis_clearAnalysisData();
        };

        scannerModel.clearScanData(onClear);
    }

    @Override
    public void capture_start() {
        if (!scannerModel.isScanDataEmpty()) {
            return;
        }

        Path cacheDirectoryPath = convertStringToPath(view.option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerService.dialog.header");

        Runnable onCancel = () -> {
            view.capture_setCaptureDataList(
                    FXCollections.observableArrayList(scannerModel.copyCaptureDataList()));
            view.song_setSongDataList(
                    FXCollections.observableArrayList(scannerModel.copySongDataList()));

            showInformation.accept(header,
                    language.getString("scannerService.dialog.scanCanceled"));
        };
        Runnable onDone = () -> {
            view.capture_setCaptureDataList(
                    FXCollections.observableArrayList(scannerModel.copyCaptureDataList()));
            view.song_setSongDataList(
                    FXCollections.observableArrayList(scannerModel.copySongDataList()));

            showInformation.accept(header, language.getString("scannerService.dialog.scanDone"));
        };

        Set<String> selectedCategorySet = view.capture_getSelectedCategorySet();
        int captureDelay = view.option_getCaptureDelay();
        int keyInputDuration = view.option_getKeyInputDuration();

        scannerModel.startCollectionScan(onDone, onCancel,
                databaseRepository.categoryNameSongListMap(), databaseRepository.getTitleTool(),
                selectedCategorySet, cacheDirectoryPath, captureDelay, keyInputDuration);
    }

    @Override
    public void capture_stop() {
        scannerModel.stopCollectionScan();
    }

    @Override
    public void song_openLinkEditor(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        Stage stage = LinkEditorStage.create();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(windowSupplier.get());

        LinkEditor.LinkEditorView linkEditorView = new LinkEditorViewImpl(stage);
        Mvp.linkViewAndPresenter(linkEditorView,
                new LinkEditorPresenterImpl(scannerModel, cacheDirectoryPath, id, () -> {
                    view.capture_refresh();
                    view.song_refresh();
                }));

        linkEditorView.startView();
    }

    @Override
    public void analysis_clearAnalysisData() {
        Runnable onClear = () -> {
            view.analysis_setAnalysisDataList(FXCollections.emptyObservableList());
            view.uploader_setNewRecordDataList(FXCollections.emptyObservableList());
            view.analysis_setProgressBarValue(0);
            view.analysis_setProgressLabelText(null);
        };

        scannerModel.clearAnalysisData(onClear);
    }

    @Override
    public void analysis_openAnalysisDataViewer(int id) {
        Path cacheDirectoryPath = convertStringToPath(view.option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        if (analysisDataViewerView == null) {
            Stage stage = AnalysisDataViewerStage.create();
            stage.initOwner(windowSupplier.get());

            analysisDataViewerView = new AnalysisDataViewerViewImpl(stage);
            Mvp.linkViewAndPresenter(analysisDataViewerView,
                    new AnalysisDataViewerPresenterImpl(scannerModel, () -> {
                        analysisDataViewerView = null; // NOPMD
                    }));
        }

        analysisDataViewerView.startView(cacheDirectoryPath, id);
    }

    @Override
    public void analysis_startAnalysis() {
        if (scannerModel.isScanDataEmpty() || !scannerModel.isAnalysisDataEmpty()) {
            return;
        }

        Path cacheDirectoryPath = convertStringToPath(view.option_getCacheDirectory());
        if (cacheDirectoryPath == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerService.dialog.header");

        Runnable onCancel = () -> {
            uploader_refresh();
            showInformation.accept(header,
                    language.getString("scannerService.dialog.analysisCanceled"));
        };
        Runnable onDone = () -> {
            uploader_refresh();
            showInformation.accept(header,
                    language.getString("scannerService.dialog.analysisDone"));
        };

        Runnable onDataReady = () -> view.analysis_setAnalysisDataList(
                FXCollections.observableArrayList(scannerModel.copyAnalysisDataList()));

        Consumer<Double> onUpdateProgress = value -> {
            view.analysis_setProgressBarValue(value);
            view.analysis_setProgressLabelText(String.format("%.2f%%", value * 100));
        };

        int analysisThreadCount = view.option_getAnalysisThreadCount();

        scannerModel.starAnalysis(onUpdateProgress, onDataReady, onDone, onCancel,
                cacheDirectoryPath, analysisThreadCount);
    }

    @Override
    public void analysis_stopAnalysis() {
        scannerModel.stopAnalysis();
    }

    @Override
    public void uploader_refresh() {
        if (scannerModel.isAnalysisDataEmpty()) {
            return;
        }

        Runnable onDone = () -> view.uploader_setNewRecordDataList(
                FXCollections.observableArrayList(scannerModel.copyNewRecordDataList()));

        scannerModel.collectNewRecord(onDone, recordModel);
    }

    @Override
    public void uploader_startUpload(long count) {
        if (scannerModel.isNewRecordDataEmpty()) {
            return;
        }

        Language language = Language.getInstance();

        {
            String header = language.getString("scanner.uploader.dialog.header");
            String content = language.getFormatString("scanner.uploader.dialog.content", count);

            if (!showConfirmation.test(header, content)) {
                return;
            }
        }

        Path accountPath = convertStringToPath(view.option_getAccountFile());
        if (accountPath == null) {
            return;
        }

        int recordUploadDelay = view.option_getRecordUploadDelay();

        Runnable onCancel;
        Runnable onDone;
        {
            String header = language.getString("scannerService.dialog.header");

            onCancel = () -> showInformation.accept(header,
                    language.getString("scannerService.dialog.uploadCanceled"));
            onDone = () -> showInformation.accept(header,
                    language.getString("scannerService.dialog.uploadDone"));
        }

        scannerModel.startUpload(onDone, onCancel, databaseRepository, recordModel, accountPath,
                recordUploadDelay);
    }

    @Override
    public void uploader_stopUpload() {
        scannerModel.stopUpload();
    }

    @Override
    public void option_openCacheDirectorySelector() {
        File file = openCacheDirectorySelector();
        if (file == null) {
            return;
        }

        Path path = file.toPath();
        try {
            scannerModel.validateCacheDirectory(path);
        } catch (IOException e) {
            String header =
                    Language.getInstance().getString("scanner.option.dialog.invalidDirectory");
            showError.accept(header, e);

            return;
        }

        path = new PathHelper(path).toRelativeOfOrNot(INITIAL_DIRECTORY);
        if (path != null) {
            view.option_setCacheDirectory(path.toString());
        }
    }

    @Override
    public void option_openAccountFileSelector() {
        File file = openAccountFileSelector();
        if (file == null) {
            return;
        }

        Path path = new PathHelper(file.toPath()).toRelativeOfOrNot(INITIAL_DIRECTORY);
        if (path != null) {
            view.option_setAccountFile(path.toString());
        }
    }
}
