package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.macro.infrastructure.database.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.provider.RepositoryProvider;
import com.github.johypark97.varchivemacro.macro.provider.ServiceProvider;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.service.AnalysisService;
import com.github.johypark97.varchivemacro.macro.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.service.UploadService;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.ui.presenter.CaptureViewer.CaptureViewerView;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Scanner.ScannerPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Scanner.ScannerView;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Scanner.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Scanner.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.ui.stage.AnalysisDataViewerStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.CaptureViewerStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.LinkEditorStage;
import com.github.johypark97.varchivemacro.macro.ui.view.AnalysisDataViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.CaptureViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.LinkEditorViewImpl;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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

    private final RepositoryProvider repositoryProvider;
    private final ServiceProvider serviceProvider;

    private final BiConsumer<String, String> showInformation;
    private final BiConsumer<String, Throwable> showError;
    private final BiPredicate<String, String> showConfirmation;
    private final Supplier<Window> windowSupplier;

    private final NativeKeyListener scannerNativeKeyListener;

    private AnalysisDataViewerView analysisDataViewerView;
    private CaptureViewerView captureViewerView;

    @MvpView
    public ScannerView view;

    public ScannerPresenterImpl(RepositoryProvider repositoryProvider,
            ServiceProvider serviceProvider, BiConsumer<String, String> showInformation,
            BiConsumer<String, Throwable> showError, BiPredicate<String, String> showConfirmation,
            Supplier<Window> windowSupplier) {
        this.repositoryProvider = repositoryProvider;
        this.serviceProvider = serviceProvider;

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
        DatabaseRepository databaseRepository = repositoryProvider.getDatabaseRepository();

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

    private void updateConfig() {
        ScannerConfig scannerConfig = new ScannerConfig();

        scannerConfig.accountFile = view.option_getAccountFile();
        scannerConfig.analysisThreadCount = view.option_getAnalysisThreadCount();
        scannerConfig.cacheDirectory = view.option_getCacheDirectory();
        scannerConfig.captureDelay = view.option_getCaptureDelay();
        scannerConfig.keyInputDuration = view.option_getKeyInputDuration();
        scannerConfig.recordUploadDelay = view.option_getRecordUploadDelay();
        scannerConfig.selectedCategorySet = view.capture_getSelectedCategorySet();

        repositoryProvider.getConfigRepository().setScannerConfig(scannerConfig);
    }

    private void onTaskFailed(String header, Throwable throwable) {
        LOGGER.atError().setCause(throwable).log(header);
        showError.accept(header, throwable);
    }

    @Override
    public void onStartView() {
        ScannerConfig scannerConfig = repositoryProvider.getConfigRepository().getScannerConfig();

        view.option_setCacheDirectory(scannerConfig.cacheDirectory);
        view.option_setupCaptureDelaySlider(ScannerConfig.CAPTURE_DELAY_DEFAULT,
                ScannerConfig.CAPTURE_DELAY_MAX, ScannerConfig.CAPTURE_DELAY_MIN,
                scannerConfig.captureDelay);
        view.option_setupKeyInputDurationSlider(ScannerConfig.KEY_INPUT_DURATION_DEFAULT,
                ScannerConfig.KEY_INPUT_DURATION_MAX, ScannerConfig.KEY_INPUT_DURATION_MIN,
                scannerConfig.keyInputDuration);
        view.option_setupAnalysisThreadCountSlider(ScannerConfig.ANALYSIS_THREAD_COUNT_DEFAULT,
                ScannerConfig.ANALYSIS_THREAD_COUNT_MAX, scannerConfig.analysisThreadCount);
        view.option_setAccountFile(scannerConfig.accountFile);
        view.option_setupRecordUploadDelaySlider(ScannerConfig.RECORD_UPLOAD_DELAY_DEFAULT,
                ScannerConfig.RECORD_UPLOAD_DELAY_MAX, ScannerConfig.RECORD_UPLOAD_DELAY_MIN,
                scannerConfig.recordUploadDelay);

        viewer_setSongTreeViewRoot(null);

        view.capture_setTabList(repositoryProvider.getDatabaseRepository().categoryNameList());
        view.capture_setSelectedCategorySet(scannerConfig.selectedCategorySet);

        FxHookWrapper.addKeyListener(scannerNativeKeyListener);
    }

    @Override
    public void onStopView() {
        FxHookWrapper.removeKeyListener(scannerNativeKeyListener);

        updateConfig();
    }

    @Override
    public void viewer_updateSongTreeViewFilter(String filter) {
        viewer_setSongTreeViewRoot(filter);
    }

    @Override
    public void viewer_showRecord(int id) {
        DatabaseRepository databaseRepository = repositoryProvider.getDatabaseRepository();
        RecordRepository recordRepository = repositoryProvider.getRecordRepository();

        SongDatabase.Song song = databaseRepository.getSong(id);
        StringBuilder builder = new StringBuilder(32);
        builder.append("Title: ").append(song.title()).append("\nComposer: ")
                .append(song.composer());
        view.viewer_setSongInformationText(builder.toString());

        ViewerRecordData data = new ViewerRecordData();
        recordRepository.getRecordList(id).forEach(x -> {
            int column = x.pattern.getWeight();
            int row = x.button.getWeight();

            data.maxCombo[row][column] = x.maxCombo;
            data.rate[row][column] = x.rate;
        });
        view.viewer_setRecordData(data);
    }

    @Override
    public void capture_openCaptureViewer(int id) {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        updateConfig();

        BufferedImage image;
        try {
            image = collectionScanService.getCaptureImage(id);
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
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        Runnable onClear = () -> {
            view.capture_setCaptureDataList(FXCollections.emptyObservableList());
            view.song_setSongDataList(FXCollections.emptyObservableList());

            analysis_clearAnalysisData();
        };

        collectionScanService.clearScanData(onClear);
    }

    @Override
    public void capture_start() {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        if (!collectionScanService.isReady_collectionScan()) {
            return;
        }

        updateConfig();

        Task<Void> task = collectionScanService.createTask_collectionScan();
        if (task == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerFxService.dialog.header");

        task.setOnFailed(event -> onTaskFailed("CollectionScanTask exception",
                event.getSource().getException()));

        task.setOnCancelled(event -> {
            view.capture_setCaptureDataList(
                    FXCollections.observableArrayList(collectionScanService.copyCaptureDataList()));
            view.song_setSongDataList(
                    FXCollections.observableArrayList(collectionScanService.copySongDataList()));

            showInformation.accept(header,
                    language.getString("scannerFxService.dialog.scanCanceled"));
        });

        task.setOnSucceeded(event -> {
            view.capture_setCaptureDataList(
                    FXCollections.observableArrayList(collectionScanService.copyCaptureDataList()));
            view.song_setSongDataList(
                    FXCollections.observableArrayList(collectionScanService.copySongDataList()));

            showInformation.accept(header, language.getString("scannerFxService.dialog.scanDone"));
        });

        CompletableFuture.runAsync(task);
    }

    @Override
    public void capture_stop() {
        serviceProvider.getScannerService().stopTask_collectionScan();
    }

    @Override
    public void song_openLinkEditor(int id) {
        updateConfig();

        Stage stage = LinkEditorStage.create();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(windowSupplier.get());

        LinkEditor.LinkEditorView linkEditorView = new LinkEditorViewImpl(stage);
        Mvp.linkViewAndPresenter(linkEditorView,
                new LinkEditorPresenterImpl(serviceProvider, id, () -> {
                    view.capture_refresh();
                    view.song_refresh();
                }));

        linkEditorView.startView();
    }

    @Override
    public void analysis_clearAnalysisData() {
        AnalysisService analysisService = serviceProvider.getAnalysisService();

        Runnable onClear = () -> {
            view.analysis_setAnalysisDataList(FXCollections.emptyObservableList());
            view.uploader_setNewRecordDataList(FXCollections.emptyObservableList());
            view.analysis_setProgressBarValue(0);
            view.analysis_setProgressLabelText(null);
        };

        analysisService.clearAnalysisData(onClear);
    }

    @Override
    public void analysis_openAnalysisDataViewer(int id) {
        updateConfig();

        if (analysisDataViewerView == null) {
            Stage stage = AnalysisDataViewerStage.create();
            stage.initOwner(windowSupplier.get());

            analysisDataViewerView = new AnalysisDataViewerViewImpl(stage);
            Mvp.linkViewAndPresenter(analysisDataViewerView,
                    new AnalysisDataViewerPresenterImpl(serviceProvider, () -> {
                        analysisDataViewerView = null; // NOPMD
                    }));
        }

        analysisDataViewerView.startView(id);
    }

    @Override
    public void analysis_startAnalysis() {
        AnalysisService analysisService = serviceProvider.getAnalysisService();

        if (!analysisService.isReady_analysis()) {
            return;
        }

        updateConfig();

        Runnable onDataReady = () -> view.analysis_setAnalysisDataList(
                FXCollections.observableArrayList(analysisService.copyAnalysisDataList()));

        Task<Void> task = analysisService.createTask_analysis(onDataReady);
        if (task == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerFxService.dialog.header");

        task.setOnFailed(
                event -> onTaskFailed("AnalysisTask exception", event.getSource().getException()));

        task.setOnCancelled(event -> {
            uploader_refresh();
            showInformation.accept(header,
                    language.getString("scannerFxService.dialog.analysisCanceled"));
        });

        task.setOnSucceeded(event -> {
            uploader_refresh();
            showInformation.accept(header,
                    language.getString("scannerFxService.dialog.analysisDone"));
        });

        task.progressProperty().addListener((observable, oldValue, newValue) -> {
            view.analysis_setProgressBarValue(newValue.doubleValue());
            view.analysis_setProgressLabelText(
                    String.format("%.2f%%", newValue.doubleValue() * 100));
        });

        CompletableFuture.runAsync(task);
    }

    @Override
    public void analysis_stopAnalysis() {
        serviceProvider.getAnalysisService().stopTask_analysis();
    }

    @Override
    public void uploader_refresh() {
        UploadService uploadService = serviceProvider.getUploadService();

        if (!uploadService.isReady_collectNewRecord()) {
            return;
        }

        Task<Void> task = uploadService.createTask_collectNewRecord();
        if (task == null) {
            return;
        }

        task.setOnFailed(event -> onTaskFailed("CollectNewRecordTask exception",
                event.getSource().getException()));

        task.setOnSucceeded(event -> view.uploader_setNewRecordDataList(
                FXCollections.observableArrayList(uploadService.copyNewRecordDataList())));

        CompletableFuture.runAsync(task);
    }

    @Override
    public void uploader_startUpload(long count) {
        UploadService uploadService = serviceProvider.getUploadService();

        if (!uploadService.isReady_upload()) {
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

        updateConfig();

        Task<Void> task = uploadService.createTask_startUpload();
        if (task == null) {
            return;
        }

        task.setOnFailed(
                event -> onTaskFailed("UploadTask exception", event.getSource().getException()));

        {
            String header = language.getString("scannerFxService.dialog.header");

            task.setOnCancelled(event -> showInformation.accept(header,
                    language.getString("scannerFxService.dialog.uploadCanceled")));

            task.setOnSucceeded(event -> showInformation.accept(header,
                    language.getString("scannerFxService.dialog.uploadDone")));
        }

        CompletableFuture.runAsync(task);
    }

    @Override
    public void uploader_stopUpload() {
        serviceProvider.getUploadService().stopTask_upload();
    }

    @Override
    public void option_openCacheDirectorySelector() {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        File file = openCacheDirectorySelector();
        if (file == null) {
            return;
        }

        Path path = file.toPath();
        try {
            collectionScanService.validateCacheDirectory(path);
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
