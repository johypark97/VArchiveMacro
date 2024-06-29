package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrTestModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrToolModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheCaptureTask;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl extends AbstractMvpPresenter<HomePresenter, HomeView>
        implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);
    private static final String EXCEPTION_LOG_MESSAGE = "Exception";

    private static final Path INITIAL_DIRECTORY = Path.of("").toAbsolutePath();

    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<OcrTestModel> ocrTestModelReference;
    private WeakReference<OcrToolModel> ocrToolModelReference;

    private WeakReference<LiveTesterPresenter> liveTesterPresenterReference;

    private FilteredList<SongData> filteredDlcSongDataList;

    public void linkModel(DatabaseModel databaseModel, OcrTestModel ocrTestModel,
            OcrToolModel ocrToolModel) {
        databaseModelReference = new WeakReference<>(databaseModel);
        ocrTestModelReference = new WeakReference<>(ocrTestModel);
        ocrToolModelReference = new WeakReference<>(ocrToolModel);
    }

    public void linkPresenter(LiveTesterPresenter liveTesterPresenter) {
        liveTesterPresenterReference = new WeakReference<>(liveTesterPresenter);
    }

    private DatabaseModel getDatabaseModel() {
        return databaseModelReference.get();
    }

    private OcrTestModel getOcrTestModel() {
        return ocrTestModelReference.get();
    }

    private OcrToolModel getOcrToolModel() {
        return ocrToolModelReference.get();
    }

    private LiveTesterPresenter getLiveTesterPresenter() {
        return liveTesterPresenterReference.get();
    }

    private void defaultOnThrow(Throwable throwable) {
        LOGGER.atError().log(EXCEPTION_LOG_MESSAGE, throwable);
        AlertBuilder.error().setThrowable(throwable).alert.showAndWait();
    }

    private void defaultOnTaskRunning() {
        AlertBuilder.warning().setContentText("The task is running.").alert.showAndWait();
    }

    private void defaultOnTaskNotRunning() {
        AlertBuilder.warning().setContentText("The task is not running.").alert.showAndWait();
    }

    private Path openDirectorySelector(String title, Window ownerWindow) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        directoryChooser.setTitle(title);

        File file = directoryChooser.showDialog(ownerWindow);
        if (file == null) {
            return null;
        }

        return file.toPath();
    }

    @Override
    protected HomePresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        // @formatter:off
        getOcrTestModel().setupOcrTestService()
                .setDlcSongList(getDatabaseModel().getDlcSongList())
                .setTitleTool(getDatabaseModel().getTitleTool())
                .setOnDone(() -> AlertBuilder.information().setContentText("OcrTest done.").alert.showAndWait())
                .setOnCancel(() -> AlertBuilder.information().setContentText("OcrTest canceled.").alert.showAndWait())
                .setOnThrow(this::defaultOnThrow)
                .setOnUpdateProgress(getView()::ocrTester_updateProgressIndicator)
                .build();

        getOcrToolModel().setupOcrCacheCaptureService()
                .setDlcSongList(getDatabaseModel().getDlcSongList())
                .setOnCancel(() -> AlertBuilder.information().setContentText("OcrCapture canceled.").alert.showAndWait())
                .setOnDone(() -> AlertBuilder.information().setContentText("OcrCapture done.").alert.showAndWait())
                .setOnThrow(this::defaultOnThrow)
                .build();

        getOcrToolModel().setupOcrCacheClassificationService()
                .setDlcSongList(getDatabaseModel().getDlcSongList())
                .setTitleTool(getDatabaseModel().getTitleTool())
                .setOnCancel(() -> AlertBuilder.information().setContentText("OcrCacheClassification canceled.").alert.showAndWait())
                .setOnDone(() -> AlertBuilder.information().setContentText("OcrCacheClassification done.").alert.showAndWait())
                .setOnThrow(this::defaultOnThrow)
                .setOnUpdateProgress(getView()::ocrCacheClassifier_updateProgressIndicator)
                .build();

        getOcrToolModel().setupOcrGroundTruthGenerationService()
                .setDlcSongList(getDatabaseModel().getDlcSongList())
                .setTitleTool(getDatabaseModel().getTitleTool())
                .setOnCancel(() -> AlertBuilder.information().setContentText("OcrGroundTruthGeneration canceled.").alert.showAndWait())
                .setOnDone(() -> AlertBuilder.information().setContentText("OcrGroundTruthGeneration done.").alert.showAndWait())
                .setOnThrow(this::defaultOnThrow)
                .setOnUpdateProgress(getView()::ocrGroundTruthGenerator_updateProgressIndicator)
                .build();
        // @formatter:on

        return true;
    }

    @Override
    protected boolean terminate() {
        if (getLiveTesterPresenter().isStarted()) {
            if (!getLiveTesterPresenter().stopPresenter()) {
                return false;
            }
        }

        if (ServiceManager.getInstance().isRunningAny()) {
            AlertBuilder.warning().setHeaderText("Unable to exit.")
                    .setContentText("Some tasks are still running.").alert.showAndWait();
            return false;
        }

        return true;
    }

    @Override
    public void onViewShowing_viewer_linkTableView(TableView<SongData> tableView) {
        filteredDlcSongDataList = new FilteredList<>(
                FXCollections.observableArrayList(getDatabaseModel().getSongDataList()));

        SortedList<SongData> list = new SortedList<>(filteredDlcSongDataList);
        list.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(list);
    }

    @Override
    public void onViewShowing_viewer_setFilterColumn(ComboBox<SongDataProperty> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(SongDataProperty.values()));
        comboBox.getSelectionModel().select(SongDataProperty.TITLE);
    }

    @Override
    public void onViewShowing_ocrTester_linkTableView(TableView<OcrTestData> tableView) {
        SortedList<OcrTestData> list = new SortedList<>(getOcrTestModel().getOcrTestDataList());
        list.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(list);
    }

    @Override
    public void onViewShowing_ocrCacheCapturer_setupCaptureDelayLinker(
            SliderTextFieldLinker.Initializer initializer) {
        initializer.apply(OcrCacheCaptureTask.CAPTURE_DELAY_DEFAULT,
                OcrCacheCaptureTask.CAPTURE_DELAY_MAX, OcrCacheCaptureTask.CAPTURE_DELAY_MIN,
                OcrCacheCaptureTask.CAPTURE_DELAY_DEFAULT);
    }

    @Override
    public void onViewShowing_ocrCacheCapturer_setupKeyInputDelayLinker(
            SliderTextFieldLinker.Initializer initializer) {
        initializer.apply(OcrCacheCaptureTask.KEY_INPUT_DELAY_DEFAULT,
                OcrCacheCaptureTask.KEY_INPUT_DELAY_MAX, OcrCacheCaptureTask.KEY_INPUT_DELAY_MIN,
                OcrCacheCaptureTask.KEY_INPUT_DELAY_DEFAULT);
    }

    @Override
    public void onViewShowing_ocrCacheCapturer_setupKeyInputDurationLinker(
            SliderTextFieldLinker.Initializer initializer) {
        initializer.apply(OcrCacheCaptureTask.KEY_INPUT_DURATION_DEFAULT,
                OcrCacheCaptureTask.KEY_INPUT_DURATION_MAX,
                OcrCacheCaptureTask.KEY_INPUT_DURATION_MIN,
                OcrCacheCaptureTask.KEY_INPUT_DURATION_DEFAULT);
    }

    @Override
    public void viewer_onUpdateTableFilter(String regex, SongDataProperty property) {
        if (filteredDlcSongDataList == null) {
            return;
        }

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Function<SongData, String> valueGetter = switch (property) {
            case ID -> x -> String.valueOf(x.getId());
            case TITLE -> SongData::getTitle;
            case REMOTE_TITLE -> SongData::getRemoteTitle;
            case COMPOSER -> SongData::getComposer;
            case DLC -> SongData::getDlc;
            case PRIORITY -> x -> String.valueOf(x.getPriority());
        };

        filteredDlcSongDataList.setPredicate(x -> pattern.matcher(valueGetter.apply(x)).find());
    }

    @Override
    public void checker_onValidateDatabase() {
        getDatabaseModel().validateDatabase(getView()::checker_setResultText);
    }

    @Override
    public void checker_onCompareDatabaseWithRemote() {
        Consumer<String> onDone = getView()::checker_setResultText;
        Consumer<Throwable> onThrow = this::defaultOnThrow;

        getDatabaseModel().compareDatabaseWithRemote(onDone, onThrow);
    }

    @Override
    public Path ocrTester_onSelectCacheDirectory(Stage stage) {
        return openDirectorySelector("Select OcrTester cache directory", stage);
    }

    @Override
    public Path ocrTester_onSelectTessdataDirectory(Stage stage) {
        return openDirectorySelector("Select OcrTester tessdata directory", stage);
    }

    @Override
    public void ocrTester_onStart(String cacheDirectory, String tessdataDirectory,
            String tessdataLanguage) {
        Path cachePath;
        Path tessdataPath;
        try {
            cachePath = Path.of(cacheDirectory);
            tessdataPath = Path.of(tessdataDirectory);
        } catch (InvalidPathException e) {
            defaultOnThrow(e);
            return;
        }

        if (!getOcrTestModel().startOcrTestService(cachePath, tessdataPath, tessdataLanguage)) {
            defaultOnTaskRunning();
        }
    }

    @Override
    public void ocrTester_onStop() {
        if (!getOcrTestModel().stopOcrTestService()) {
            defaultOnTaskNotRunning();
        }
    }

    @Override
    public Path ocrCacheCapturer_onSelectOutputDirectory(Stage stage) {
        return openDirectorySelector("Select OcrCacheCapturer output directory", stage);
    }

    @Override
    public void ocrCacheCapturer_onStart(int captureDelay, int keyInputDelay, int keyInputDuration,
            String outputDirectory) {
        Path outputPath;
        try {
            outputPath = Path.of(outputDirectory);
        } catch (InvalidPathException e) {
            defaultOnThrow(e);
            return;
        }

        getOcrToolModel().startOcrCacheCaptureService(captureDelay, keyInputDelay, keyInputDuration,
                outputPath);
    }

    @Override
    public void ocrCacheCapturer_onStop() {
        if (!getOcrToolModel().stopOcrCacheCaptureService()) {
            defaultOnTaskNotRunning();
        }
    }

    @Override
    public Path ocrCacheClassifier_onSelectInputDirectory(Stage stage) {
        return openDirectorySelector("Select OcrCacheClassifier input directory", stage);
    }

    @Override
    public Path ocrCacheClassifier_onSelectOutputDirectory(Stage stage) {
        return openDirectorySelector("Select OcrCacheClassifier output directory", stage);
    }

    @Override
    public void ocrCacheClassifier_onStart(String inputDirectory, String outputDirectory) {
        Path inputPath;
        Path outputPath;
        try {
            inputPath = Path.of(inputDirectory);
            outputPath = Path.of(outputDirectory);
        } catch (InvalidPathException e) {
            defaultOnThrow(e);
            return;
        }

        if (!getOcrToolModel().startOcrCacheClassificationService(inputPath, outputPath)) {
            defaultOnTaskRunning();
        }
    }

    @Override
    public void ocrCacheClassifier_onStop() {
        if (!getOcrToolModel().stopOcrCacheClassificationService()) {
            defaultOnTaskNotRunning();
        }
    }

    @Override
    public Path ocrGroundTruthGenerator_onSelectInputDirectory(Stage stage) {
        return openDirectorySelector("Select OcrGroundTruthGenerator input directory", stage);
    }

    @Override
    public Path ocrGroundTruthGenerator_onSelectOutputDirectory(Stage stage) {
        return openDirectorySelector("Select OcrGroundTruthGenerator output directory", stage);
    }

    @Override
    public void ocrGroundTruthGenerator_onStart(String inputDirectory, String outputDirectory) {
        Path inputPath;
        Path outputPath;
        try {
            inputPath = Path.of(inputDirectory);
            outputPath = Path.of(outputDirectory);
        } catch (InvalidPathException e) {
            defaultOnThrow(e);
            return;
        }

        if (!getOcrToolModel().startOcrGroundTruthGenerationService(inputPath, outputPath)) {
            defaultOnTaskRunning();
        }
    }

    @Override
    public void ocrGroundTruthGenerator_onStop() {
        if (!getOcrToolModel().stopOcrGroundTruthGenerationService()) {
            defaultOnTaskNotRunning();
        }
    }

    @Override
    public Path liveTester_onSelectTessdataDirectory(Stage stage) {
        return openDirectorySelector("Select LiveTester tessdata directory", stage);
    }

    @Override
    public void liveTester_onOpen(String tessdataDirectory, String tessdataLanguage) {
        if (getLiveTesterPresenter().isStarted()) {
            getLiveTesterPresenter().focusView();
            return;
        }

        Path tessdataPath;
        try {
            tessdataPath = Path.of(tessdataDirectory);
        } catch (InvalidPathException e) {
            defaultOnThrow(e);
            return;
        }

        LiveTester.StartData data = new LiveTester.StartData();

        data.tessdataLanguage = tessdataLanguage;
        data.tessdataPath = tessdataPath;

        getLiveTesterPresenter().setStartData(data);
        getLiveTesterPresenter().startPresenter();
    }

    @Override
    public void liveTester_onClose() {
        getLiveTesterPresenter().stopPresenter();
    }
}
