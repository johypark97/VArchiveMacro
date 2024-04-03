package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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

    public void linkModel(ConfigModel configModel, DatabaseModel databaseModel,
            RecordModel recordModel, ScannerModel scannerModel) {
        configModelReference = new WeakReference<>(configModel);
        databaseModelReference = new WeakReference<>(databaseModel);
        recordModelReference = new WeakReference<>(recordModel);
        scannerModelReference = new WeakReference<>(scannerModel);
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

    @Override
    public void onViewShow_setupService() {
        String header = "Collection Scanner";

        Consumer<Throwable> onThrow = throwable -> {
            String message = "Scanner service exception";

            LOGGER.atError().log(message, throwable);
            getView().showError(message, throwable);
        };

        Runnable onCancel = () -> getView().showInformation(header, "Scan canceled.");
        Runnable onDone = () -> getView().showInformation(header, "Scan done.");

        getScannerModel().setupService(onDone, onCancel, onThrow);
    }

    @Override
    public void onViewShow_setupCacheDirectory(TextField textField) {
        textField.setText(getConfigModel().getScannerConfig().cacheDirectory.toString());
    }

    @Override
    public void onViewShow_setupCaptureDelayLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(ScannerConfig.CAPTURE_DELAY_DEFAULT);
        linker.setLimitMax(ScannerConfig.CAPTURE_DELAY_MAX);
        linker.setLimitMin(ScannerConfig.CAPTURE_DELAY_MIN);
        linker.setValue(getConfigModel().getScannerConfig().captureDelay);
    }

    @Override
    public void onViewShow_setupKeyInputDurationLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(ScannerConfig.KEY_INPUT_DURATION_DEFAULT);
        linker.setLimitMax(ScannerConfig.KEY_INPUT_DURATION_MAX);
        linker.setLimitMin(ScannerConfig.KEY_INPUT_DURATION_MIN);
        linker.setValue(getConfigModel().getScannerConfig().keyInputDuration);
    }

    @Override
    public void onViewShow_setupAccountFile(TextField textField) {
        textField.setText(getConfigModel().getScannerConfig().accountFile.toString());
    }

    @Override
    public void onViewShow_setupRecordUploadDelayLinker(SliderTextFieldLinker linker) {
        linker.setDefaultValue(ScannerConfig.RECORD_UPLOAD_DELAY_DEFAULT);
        linker.setLimitMax(ScannerConfig.RECORD_UPLOAD_DELAY_MAX);
        linker.setLimitMin(ScannerConfig.RECORD_UPLOAD_DELAY_MIN);
        linker.setValue(getConfigModel().getScannerConfig().recordUploadDelay);
    }

    @Override
    public boolean onViewShow_loadDatabase() {
        try {
            getDatabaseModel().load();
        } catch (IOException e) {
            getView().getScannerFrontController().showForbiddenMark();
            getView().showError("Database loading error", e);
            LOGGER.atError().log("DatabaseModel loading exception", e);
            return false;
        } catch (Exception e) {
            getView().getScannerFrontController().showForbiddenMark();
            getView().showError("Critical database loading error", e);
            LOGGER.atError().log("Critical DatabaseModel loading exception", e);
            throw e;
        }

        getView().scanner_capture_setTabList(getDatabaseModel().getDlcTabList());

        Set<String> selectedTabSet = getConfigModel().getScannerConfig().selectedTabSet;
        getView().scanner_capture_setSelectedTabSet(selectedTabSet);

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
            LOGGER.atError().log("RecordModel loading exception", e);
            return;
        } catch (Exception e) {
            getView().getScannerFrontController().showDjNameInput();
            getView().showError("Critical local records loading error", e);
            LOGGER.atError().log("Critical RecordModel loading exception", e);
            throw e;
        }

        getView().getScannerFrontController().showScanner();
    }

    @Override
    public void scanner_front_onLoadRemoteRecord(String djName) {
        getView().getScannerFrontController().hideDjNameInputError();

        if (djName.isBlank()) {
            getView().getScannerFrontController().showDjNameInputError("DJ Name is blank.");
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

            getView().getScannerFrontController().hideDjNameInput();
            getView().getScannerFrontController().showScanner();
        };

        getRecordModel().loadRemote(djName, onDone, onThrow);
    }

    @Override
    public void scanner_viewer_onShowSongTree(TreeView<ViewerTreeData> treeView, String filter) {
        String normalizedFilter;
        if (filter == null || filter.isBlank()) {
            normalizedFilter = null; // NOPMD
        } else {
            normalizedFilter = VIEWER_TITLE_NORMALIZER.apply(filter.trim());
        }

        TreeItem<ViewerTreeData> rootNode = new TreeItem<>();
        getDatabaseModel().getDlcTapSongMap().forEach((tab, songList) -> {
            TreeItem<ViewerTreeData> dlcNode = new TreeItem<>(new ViewerTreeData(tab));
            dlcNode.setExpanded(normalizedFilter != null);
            rootNode.getChildren().add(dlcNode);

            Stream<LocalDlcSong> stream = songList.stream();
            if (normalizedFilter != null) {
                stream = stream.filter(
                        x -> VIEWER_TITLE_NORMALIZER.apply(x.title).contains(normalizedFilter));
            }
            stream.forEach(
                    song -> dlcNode.getChildren().add(new TreeItem<>(new ViewerTreeData(song))));
        });

        treeView.setRoot(rootNode);
    }

    @Override
    public ViewerRecordData scanner_viewer_onShowRecord(int id) {
        ViewerRecordData data = new ViewerRecordData();

        LocalDlcSong song = getDatabaseModel().getDlcSong(id);
        data.composer = song.composer;
        data.title = song.title;

        getRecordModel().getRecordList(id).forEach(x -> {
            int column = x.pattern.getWeight();
            int row = x.button.getWeight();

            data.maxCombo[row][column] = x.maxCombo;
            data.rate[row][column] = x.rate;
        });

        return data;
    }

    @Override
    public void scanner_capture_onStart(Set<String> selectedTabSet, String cacheDirectory,
            int captureDelay, int keyInputDuration) {
        Path cacheDirectoryPath;
        try {
            cacheDirectoryPath = Path.of(cacheDirectory);
        } catch (InvalidPathException e) {
            getView().showError("Invalid cache directory.", e);
            return;
        }

        getScannerModel().startCollectionScan(getDatabaseModel().getDlcTapSongMap(),
                getDatabaseModel().getTitleTool(), selectedTabSet, cacheDirectoryPath, captureDelay,
                keyInputDuration);
    }

    @Override
    public void scanner_capture_onStop() {
        getScannerModel().stopCollectionScan();
    }

    @Override
    public Path scanner_option_onOpenCacheDirectorySelector(Window ownerWindow) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        chooser.setTitle("Select cache directory");

        File file = chooser.showDialog(ownerWindow);
        if (file == null) {
            return null;
        }

        Path path = file.toPath();
        try {
            getScannerModel().validateCacheDirectory(path);
        } catch (IOException e) {
            getView().showError("The selected cache directory is not suitable for use.", e);
            return null;
        }

        return new PathHelper(path).toRelativeOfOrNot(INITIAL_DIRECTORY);
    }

    @Override
    public Path scanner_option_onOpenAccountFileSelector(Window ownerWindow) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        chooser.setTitle("Select account file");

        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Account text file (*.txt)", "*.txt"));

        File file = chooser.showOpenDialog(ownerWindow);
        if (file == null) {
            return null;
        }

        return new PathHelper(file.toPath()).toRelativeOfOrNot(INITIAL_DIRECTORY);
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
        ScannerConfig scannerConfig = new ScannerConfig();

        scannerConfig.selectedTabSet = getView().scanner_capture_getSelectedTabSet();

        try {
            scannerConfig.cacheDirectory = Path.of(getView().scanner_option_getCacheDirectory());
        } catch (InvalidPathException ignored) {
        }

        scannerConfig.captureDelay = getView().scanner_option_getCaptureDelay();
        scannerConfig.keyInputDuration = getView().scanner_option_getKeyInputDuration();

        try {
            scannerConfig.accountFile = Path.of(getView().scanner_option_getAccountFile());
        } catch (InvalidPathException ignored) {
        }

        scannerConfig.recordUploadDelay = getView().scanner_option_getRecordUploadDelay();

        getConfigModel().setScannerConfig(scannerConfig);

        try {
            getConfigModel().save();
        } catch (IOException ignored) {
        }

        return true;
    }
}
