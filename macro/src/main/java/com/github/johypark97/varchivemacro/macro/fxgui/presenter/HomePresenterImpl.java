package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.MacroConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicensePresenter;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl extends AbstractMvpPresenter<HomePresenter, HomeView>
        implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private static final Path INITIAL_DIRECTORY = Path.of("").toAbsolutePath();

    private final Function<String, String> VIEWER_TITLE_NORMALIZER =
            x -> Normalizer.normalize(x.toLowerCase(Locale.ENGLISH), Form.NFKD);

    private WeakReference<ConfigModel> configModelReference;
    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<RecordModel> recordModelReference;
    private WeakReference<ScannerModel> scannerModelReference;
    private WeakReference<MacroModel> macroModelReference;

    private WeakReference<AnalysisDataViewerPresenter> analysisDataViewerPresenterReference;
    private WeakReference<CaptureViewerPresenter> captureViewerPresenterReference;
    private WeakReference<LinkEditorPresenter> linkEditorPresenterReference;
    private WeakReference<OpenSourceLicensePresenter> openSourceLicensePresenterReference;

    public void linkModel(ConfigModel configModel, DatabaseModel databaseModel,
            RecordModel recordModel, ScannerModel scannerModel, MacroModel macroModel) {
        configModelReference = new WeakReference<>(configModel);
        databaseModelReference = new WeakReference<>(databaseModel);
        recordModelReference = new WeakReference<>(recordModel);
        scannerModelReference = new WeakReference<>(scannerModel);
        macroModelReference = new WeakReference<>(macroModel);
    }

    public void linkPresenter(AnalysisDataViewerPresenter analysisDataViewerPresenter,
            CaptureViewerPresenter captureViewerPresenter, LinkEditorPresenter linkEditorPresenter,
            OpenSourceLicensePresenter openSourceLicensePresenter) {
        analysisDataViewerPresenterReference = new WeakReference<>(analysisDataViewerPresenter);
        captureViewerPresenterReference = new WeakReference<>(captureViewerPresenter);
        linkEditorPresenterReference = new WeakReference<>(linkEditorPresenter);
        openSourceLicensePresenterReference = new WeakReference<>(openSourceLicensePresenter);
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

    private AnalysisDataViewerPresenter getAnalysisDataViewerPresenter() {
        return analysisDataViewerPresenterReference.get();
    }

    private CaptureViewerPresenter getCaptureViewerPresenter() {
        return captureViewerPresenterReference.get();
    }

    private LinkEditorPresenter getLinkEditorPresenter() {
        return linkEditorPresenterReference.get();
    }

    private OpenSourceLicensePresenter getOpenSourceLicensePresenter() {
        return openSourceLicensePresenterReference.get();
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

        getView().scanner_viewer_setSongTreeViewRoot(rootNode);
    }

    private Path convertStringToPath(String pathString) {
        try {
            return Path.of(pathString);
        } catch (InvalidPathException e) {
            getView().showError("Invalid path.", e);
            return null;
        }
    }

    @Override
    public void onViewShow_setupService() {
        getScannerModel().setupService(throwable -> {
            String header = "Scanner service exception";

            LOGGER.atError().setCause(throwable).log(header);
            getView().showError(header, throwable);
        });

        getMacroModel().setupService(throwable -> {
            String header = "Macro service exception";

            LOGGER.atError().setCause(throwable).log(header);
            getView().showError(header, throwable);
        });
    }

    @Override
    public void onViewShow_scanner_setupCacheDirectory(TextField textField) {
        textField.setText(getConfigModel().getScannerConfig().cacheDirectory.toString());
    }

    @Override
    public void onViewShow_scanner_setupCaptureDelayLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(ScannerConfig.CAPTURE_DELAY_DEFAULT);
        linker.setLimitMax(ScannerConfig.CAPTURE_DELAY_MAX);
        linker.setLimitMin(ScannerConfig.CAPTURE_DELAY_MIN);
        linker.setValue(getConfigModel().getScannerConfig().captureDelay);
    }

    @Override
    public void onViewShow_scanner_setupKeyInputDurationLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(ScannerConfig.KEY_INPUT_DURATION_DEFAULT);
        linker.setLimitMax(ScannerConfig.KEY_INPUT_DURATION_MAX);
        linker.setLimitMin(ScannerConfig.KEY_INPUT_DURATION_MIN);
        linker.setValue(getConfigModel().getScannerConfig().keyInputDuration);
    }

    @Override
    public void onViewShow_scanner_setupAccountFile(TextField textField) {
        textField.setText(getConfigModel().getScannerConfig().accountFile.toString());
    }

    @Override
    public void onViewShow_scanner_setupRecordUploadDelayLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(ScannerConfig.RECORD_UPLOAD_DELAY_DEFAULT);
        linker.setLimitMax(ScannerConfig.RECORD_UPLOAD_DELAY_MAX);
        linker.setLimitMin(ScannerConfig.RECORD_UPLOAD_DELAY_MIN);
        linker.setValue(getConfigModel().getScannerConfig().recordUploadDelay);
    }

    @Override
    public void onViewShow_macro_setupAnalysisKey() {
        getView().macro_setAnalysisKey(getConfigModel().getMacroConfig().analysisKey);
    }

    @Override
    public void onViewShow_macro_setupCountLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(MacroConfig.COUNT_DEFAULT);
        linker.setLimitMax(MacroConfig.COUNT_MAX);
        linker.setLimitMin(MacroConfig.COUNT_MIN);
        linker.setValue(getConfigModel().getMacroConfig().count);
    }

    @Override
    public void onViewShow_macro_setupCaptureDelayLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(MacroConfig.CAPTURE_DELAY_DEFAULT);
        linker.setLimitMax(MacroConfig.CAPTURE_DELAY_MAX);
        linker.setLimitMin(MacroConfig.CAPTURE_DELAY_MIN);
        linker.setValue(getConfigModel().getMacroConfig().captureDelay);
    }

    @Override
    public void onViewShow_macro_setupCaptureDurationLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(MacroConfig.CAPTURE_DURATION_DEFAULT);
        linker.setLimitMax(MacroConfig.CAPTURE_DURATION_MAX);
        linker.setLimitMin(MacroConfig.CAPTURE_DURATION_MIN);
        linker.setValue(getConfigModel().getMacroConfig().captureDuration);
    }

    @Override
    public void onViewShow_macro_setupKeyInputDurationLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(MacroConfig.KEY_INPUT_DURATION_DEFAULT);
        linker.setLimitMax(MacroConfig.KEY_INPUT_DURATION_MAX);
        linker.setLimitMin(MacroConfig.KEY_INPUT_DURATION_MIN);
        linker.setValue(getConfigModel().getMacroConfig().keyInputDuration);
    }

    @Override
    public boolean onViewShow_loadDatabase() {
        try {
            getDatabaseModel().load();
        } catch (SQLException | IOException e) {
            getView().getScannerFrontController().showForbiddenMark();
            getView().showError("Database loading error", e);
            LOGGER.atError().setCause(e).log("DatabaseModel loading exception");
            return false;
        } catch (Exception e) {
            getView().getScannerFrontController().showForbiddenMark();
            getView().showError("Critical database loading error", e);
            LOGGER.atError().setCause(e).log("Critical DatabaseModel loading exception");
            throw e;
        }

        scanner_viewer_setSongTreeViewRoot(null);

        getView().scanner_capture_setTabList(getDatabaseModel().categoryNameList());

        Set<String> selectedCategorySet = getConfigModel().getScannerConfig().selectedCategorySet;
        getView().scanner_capture_setSelectedCategorySet(selectedCategorySet);

        return true;
    }

    @Override
    public void onViewShow_loadRecord() {
        try {
            if (!getRecordModel().loadLocal()) {
                getView().getScannerFrontController().showDjNameInput();
                return;
            }
        } catch (IOException e) {
            getView().getScannerFrontController().showDjNameInput();
            getView().showError("Local records loading error", e);
            LOGGER.atError().setCause(e).log("RecordModel loading exception");
            return;
        } catch (Exception e) {
            getView().getScannerFrontController().showDjNameInput();
            getView().showError("Critical local records loading error", e);
            LOGGER.atError().setCause(e).log("Critical RecordModel loading exception");
            throw e;
        }

        getView().getScannerFrontController().showScanner();
    }

    @Override
    public void home_onChangeLanguage(Locale locale) {
        try {
            Language.saveLocale(locale);
        } catch (IOException e) {
            getView().showError("Language changing error", e);
            LOGGER.atError().setCause(e).log("Language changing error");
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("home.dialog.languageChange.header");
        String content = language.getString("home.dialog.languageChange.content");
        getView().showInformation(header, content);
    }

    @Override
    public void home_onOpenOpenSourceLicense(Window ownerWindow) {
        OpenSourceLicense.StartData startData = new OpenSourceLicense.StartData();
        startData.ownerWindow = ownerWindow;

        getOpenSourceLicensePresenter().setStartData(startData);
        getOpenSourceLicensePresenter().startPresenter();
    }

    @Override
    public void scanner_front_onLoadRemoteRecord(String djName) {
        getView().getScannerFrontController().hideDjNameInputError();

        if (djName.isBlank()) {
            String message = Language.getInstance().getString("scannerDjNameInput.blankError");
            getView().getScannerFrontController().showDjNameInputError(message);
            return;
        }

        getView().getScannerFrontController().showLoadingMark(djName);

        BiConsumer<String, Exception> onThrow = getView()::showError;
        Consumer<Boolean> onDone = x -> {
            getView().getScannerFrontController().hideLoadingMark();

            if (Boolean.FALSE.equals(x)) {
                getView().getScannerFrontController().showDjNameInput();
                return;
            }

            getView().getScannerFrontController().showScanner();
        };

        getRecordModel().loadRemote(djName, onDone, onThrow);
    }

    @Override
    public void scanner_viewer_onUpdateSongTreeViewFilter(String filter) {
        scanner_viewer_setSongTreeViewRoot(filter);
    }

    @Override
    public ViewerRecordData scanner_viewer_onShowRecord(int id) {
        ViewerRecordData data = new ViewerRecordData();

        Song song = getDatabaseModel().getSong(id);
        data.composer = song.composer();
        data.title = song.title();

        getRecordModel().getRecordList(id).forEach(x -> {
            int column = x.pattern.getWeight();
            int row = x.button.getWeight();

            data.maxCombo[row][column] = x.maxCombo;
            data.rate[row][column] = x.rate;
        });

        return data;
    }

    @Override
    public void scanner_capture_onOpenCaptureViewer(Window ownerWindow, String cacheDirectory,
            int id) {
        Path cacheDirectoryPath = convertStringToPath(cacheDirectory);
        if (cacheDirectoryPath == null) {
            return;
        }

        BufferedImage image;
        try {
            image = getScannerModel().getCaptureImage(cacheDirectoryPath, id);
        } catch (IOException e) {
            getView().showError("Cache image reading error", e);
            return;
        }

        CaptureViewer.StartData startData = new CaptureViewer.StartData();
        startData.image = SwingFXUtils.toFXImage(image, null);
        startData.ownerWindow = ownerWindow;
        getCaptureViewerPresenter().setStartData(startData);

        if (getCaptureViewerPresenter().isStarted()
                || getCaptureViewerPresenter().startPresenter()) {
            getCaptureViewerPresenter().updateView();
        }
    }

    @Override
    public void scanner_capture_onClearScanData() {
        Runnable onClear = () -> {
            getView().scanner_capture_setCaptureDataList(FXCollections.emptyObservableList());
            getView().scanner_song_setSongDataList(FXCollections.emptyObservableList());

            scanner_analysis_onClearAnalysisData();
        };

        getScannerModel().clearScanData(onClear);
    }

    @Override
    public void scanner_capture_onStart(Set<String> selectedCategorySet, String cacheDirectory,
            int captureDelay, int keyInputDuration) {
        if (!getScannerModel().isScanDataEmpty()) {
            return;
        }

        Path cacheDirectoryPath = convertStringToPath(cacheDirectory);
        if (cacheDirectoryPath == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerService.dialog.header");

        Runnable onCancel = () -> {
            getView().scanner_capture_setCaptureDataList(
                    FXCollections.observableArrayList(getScannerModel().copyCaptureDataList()));
            getView().scanner_song_setSongDataList(
                    FXCollections.observableArrayList(getScannerModel().copySongDataList()));

            getView().showInformation(header,
                    language.getString("scannerService.dialog.scanCanceled"));
        };
        Runnable onDone = () -> {
            getView().scanner_capture_setCaptureDataList(
                    FXCollections.observableArrayList(getScannerModel().copyCaptureDataList()));
            getView().scanner_song_setSongDataList(
                    FXCollections.observableArrayList(getScannerModel().copySongDataList()));

            getView().showInformation(header, language.getString("scannerService.dialog.scanDone"));
        };

        getScannerModel().startCollectionScan(onDone, onCancel,
                getDatabaseModel().categoryNameSongListMap(), getDatabaseModel().getTitleTool(),
                selectedCategorySet, cacheDirectoryPath, captureDelay, keyInputDuration);
    }

    @Override
    public void scanner_capture_onStop() {
        getScannerModel().stopCollectionScan();
    }

    @Override
    public void scanner_song_onOpenLinkEditor(Window ownerWindow, String cacheDirectory, int id) {
        Path cacheDirectoryPath = convertStringToPath(cacheDirectory);
        if (cacheDirectoryPath == null) {
            return;
        }

        LinkEditor.StartData startData = new LinkEditor.StartData();
        startData.cacheDirectoryPath = cacheDirectoryPath;
        startData.ownerWindow = ownerWindow;
        startData.songDataId = id;
        startData.onLinkUpdate = () -> {
            getView().scanner_capture_refresh();
            getView().scanner_song_refresh();
        };

        getLinkEditorPresenter().setStartData(startData);
        getLinkEditorPresenter().startPresenter();
    }

    @Override
    public void scanner_analysis_onClearAnalysisData() {
        Runnable onClear = () -> {
            getView().scanner_analysis_setAnalysisDataList(FXCollections.emptyObservableList());
            getView().scanner_uploader_setNewRecordDataList(FXCollections.emptyObservableList());
        };

        getScannerModel().clearAnalysisData(onClear);
    }

    @Override
    public void scanner_analysis_onOpenAnalysisDataViewer(Window ownerWindow, String cacheDirectory,
            int id) {
        Path cacheDirectoryPath = convertStringToPath(cacheDirectory);
        if (cacheDirectoryPath == null) {
            return;
        }

        AnalysisDataViewer.StartData startData = new AnalysisDataViewer.StartData();
        startData.analysisDataId = id;
        startData.cacheDirectoryPath = cacheDirectoryPath;
        startData.ownerWindow = ownerWindow;
        getAnalysisDataViewerPresenter().setStartData(startData);

        if (!getAnalysisDataViewerPresenter().isStarted()) {
            getAnalysisDataViewerPresenter().startPresenter();
        } else {
            getAnalysisDataViewerPresenter().updateView();
        }
    }

    @Override
    public void scanner_analysis_onStartAnalysis(String cacheDirectory) {
        if (getScannerModel().isScanDataEmpty() || !getScannerModel().isAnalysisDataEmpty()) {
            return;
        }

        Path cacheDirectoryPath = convertStringToPath(cacheDirectory);
        if (cacheDirectoryPath == null) {
            return;
        }

        Language language = Language.getInstance();
        String header = language.getString("scannerService.dialog.header");

        Runnable onCancel = () -> {
            scanner_uploader_onRefresh();
            getView().showInformation(header,
                    language.getString("scannerService.dialog.analysisCanceled"));
        };
        Runnable onDone = () -> {
            scanner_uploader_onRefresh();
            getView().showInformation(header,
                    language.getString("scannerService.dialog.analysisDone"));
        };

        Runnable onDataReady = () -> getView().scanner_analysis_setAnalysisDataList(
                FXCollections.observableArrayList(getScannerModel().copyAnalysisDataList()));

        getScannerModel().starAnalysis(onDataReady, onDone, onCancel, cacheDirectoryPath);
    }

    @Override
    public void scanner_analysis_onStopAnalysis() {
        getScannerModel().stopAnalysis();
    }

    @Override
    public void scanner_uploader_onRefresh() {
        if (getScannerModel().isAnalysisDataEmpty()) {
            return;
        }

        Runnable onDone = () -> getView().scanner_uploader_setNewRecordDataList(
                FXCollections.observableArrayList(getScannerModel().copyNewRecordDataList()));

        getScannerModel().collectNewRecord(onDone, getRecordModel());
    }

    @Override
    public void scanner_uploader_onStartUpload(long count) {
        if (getScannerModel().isNewRecordDataEmpty()) {
            return;
        }

        Language language = Language.getInstance();

        {
            String header = language.getString("scanner.uploader.dialog.header");
            String content = language.getFormatString("scanner.uploader.dialog.content", count);

            if (!getView().showConfirmation(header, content)) {
                return;
            }
        }

        Path accountPath = convertStringToPath(getView().scanner_option_getAccountFile());
        if (accountPath == null) {
            return;
        }

        int recordUploadDelay = getView().scanner_option_getRecordUploadDelay();

        Runnable onCancel;
        Runnable onDone;
        {
            String header = language.getString("scannerService.dialog.header");

            onCancel = () -> getView().showInformation(header,
                    language.getString("scannerService.dialog.uploadCanceled"));
            onDone = () -> getView().showInformation(header,
                    language.getString("scannerService.dialog.uploadDone"));
        }

        getScannerModel().startUpload(onDone, onCancel, getDatabaseModel(), getRecordModel(),
                accountPath, recordUploadDelay);
    }

    @Override
    public void scanner_uploader_onStopUpload() {
        getScannerModel().stopUpload();
    }

    @Override
    public Path scanner_option_onOpenCacheDirectorySelector(Window ownerWindow) {
        Language language = Language.getInstance();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        chooser.setTitle(language.getString("scanner.option.dialog.cacheDirectorySelectorTitle"));

        File file = chooser.showDialog(ownerWindow);
        if (file == null) {
            return null;
        }

        Path path = file.toPath();
        try {
            getScannerModel().validateCacheDirectory(path);
        } catch (IOException e) {
            String header = language.getString("scanner.option.dialog.invalidDirectory");
            getView().showError(header, e);
            return null;
        }

        return new PathHelper(path).toRelativeOfOrNot(INITIAL_DIRECTORY);
    }

    @Override
    public Path scanner_option_onOpenAccountFileSelector(Window ownerWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        chooser.setTitle(
                Language.getInstance().getString("scanner.option.dialog.AccountFileSelectorTitle"));

        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Account text file (*.txt)", "*.txt"));

        File file = chooser.showOpenDialog(ownerWindow);
        if (file == null) {
            return null;
        }

        return new PathHelper(file.toPath()).toRelativeOfOrNot(INITIAL_DIRECTORY);
    }

    @Override
    public void macro_onStart_up() {
        AnalysisKey analysisKey = getView().macro_getAnalysisKey();
        int count = getView().macro_getCount();
        int captureDelay = getView().macro_getCaptureDelay();
        int captureDuration = getView().macro_getCaptureDuration();
        int keyInputDuration = getView().macro_getKeyInputDuration();

        getMacroModel().startMacro(analysisKey, count, captureDelay, captureDuration,
                keyInputDuration, VerticalDirection.UP);
    }

    @Override
    public void macro_onStart_down() {
        AnalysisKey analysisKey = getView().macro_getAnalysisKey();
        int count = getView().macro_getCount();
        int captureDelay = getView().macro_getCaptureDelay();
        int captureDuration = getView().macro_getCaptureDuration();
        int keyInputDuration = getView().macro_getKeyInputDuration();

        getMacroModel().startMacro(analysisKey, count, captureDelay, captureDuration,
                keyInputDuration, VerticalDirection.DOWN);
    }

    @Override
    public void macro_onStop() {
        getMacroModel().stopMacro();
    }

    @Override
    protected HomePresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        try {
            if (!getConfigModel().load()) {
                getConfigModel().save();
            }
        } catch (IOException ignored) {
        }

        return true;
    }

    @Override
    protected boolean terminate() {
        if (getCaptureViewerPresenter().isStarted()
                && !getCaptureViewerPresenter().stopPresenter()) {
            return false;
        }

        if (getLinkEditorPresenter().isStarted() && !getLinkEditorPresenter().stopPresenter()) {
            return false;
        }

        ScannerConfig scannerConfig = new ScannerConfig();
        {
            scannerConfig.selectedCategorySet = getView().scanner_capture_getSelectedCategorySet();

            try {
                scannerConfig.cacheDirectory =
                        Path.of(getView().scanner_option_getCacheDirectory());
            } catch (InvalidPathException ignored) {
            }

            scannerConfig.captureDelay = getView().scanner_option_getCaptureDelay();
            scannerConfig.keyInputDuration = getView().scanner_option_getKeyInputDuration();

            try {
                scannerConfig.accountFile = Path.of(getView().scanner_option_getAccountFile());
            } catch (InvalidPathException ignored) {
            }

            scannerConfig.recordUploadDelay = getView().scanner_option_getRecordUploadDelay();
        }
        getConfigModel().setScannerConfig(scannerConfig);

        MacroConfig macroConfig = new MacroConfig();
        {
            macroConfig.analysisKey = getView().macro_getAnalysisKey();
            macroConfig.count = getView().macro_getCount();
            macroConfig.captureDelay = getView().macro_getCaptureDelay();
            macroConfig.captureDuration = getView().macro_getCaptureDuration();
            macroConfig.keyInputDuration = getView().macro_getKeyInputDuration();
        }
        getConfigModel().setMacroConfig(macroConfig);

        try {
            getConfigModel().save();
        } catch (IOException ignored) {
        }

        return true;
    }
}
