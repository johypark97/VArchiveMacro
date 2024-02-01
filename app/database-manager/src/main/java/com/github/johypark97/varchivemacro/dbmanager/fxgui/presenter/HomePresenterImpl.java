package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.core.ServiceManager;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.Dialogs;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.OcrTestModel;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpPresenter;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

    public DatabaseModel databaseModel;
    public OcrTestModel ocrTestModel;

    public HomePresenterImpl(Supplier<HomeView> viewConstructor) {
        super(viewConstructor);
    }

    public void setModel(DatabaseModel databaseModel, OcrTestModel ocrTestModel) {
        this.databaseModel = databaseModel;
        this.ocrTestModel = ocrTestModel;
    }

    private void defaultOnThrow(Throwable throwable) {
        LOGGER.atError().log(EXCEPTION_LOG_MESSAGE, throwable);
        Platform.runLater(() -> Dialogs.showException(throwable));
    }

    private void defaultOnTaskRunning() {
        Platform.runLater(() -> Dialogs.showWarning("The task is running."));
    }

    private void defaultOnTaskNotRunning() {
        Platform.runLater(() -> Dialogs.showWarning("The task is not running."));
    }

    private Runnable showMessage(String message) {
        return () -> Platform.runLater(() -> Dialogs.showInformation(message));
    }

    private Path openDirectorySelector(Window ownerWindow) {
        String TITLE = "Select database directory";

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(INITIAL_DIRECTORY.toFile());
        directoryChooser.setTitle(TITLE);

        File file = directoryChooser.showDialog(ownerWindow);
        if (file == null) {
            return null;
        }

        return file.toPath();
    }

    @Override
    public void onSetupModel() {
        // @formatter:off
        ocrTestModel.setupOcrTestService()
                .setDlcSongList(databaseModel.getDlcSongList())
                .setTitleTool(databaseModel.getTitleTool())
                .setOnDone(showMessage("OcrTest done."))
                .setOnCancel(showMessage("OcrTest canceled."))
                .setOnThrow(this::defaultOnThrow)
                .setOnUpdateProgress(getView()::updateOcrTesterProgressIndicator)
                .build();
        // @formatter:on
    }

    @Override
    public void onLinkViewerTable(TableView<SongData> tableView) {
        SortedList<SongData> list = new SortedList<>(databaseModel.getFilteredSongList());
        list.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(list);
    }

    @Override
    public void onSetViewerTableFilterColumn(ComboBox<SongDataProperty> comboBox) {
        comboBox.setItems(FXCollections.observableArrayList(SongDataProperty.values()));
        comboBox.getSelectionModel().select(SongDataProperty.TITLE);
    }

    @Override
    public void onUpdateViewerTableFilter(String regex, SongDataProperty property) {
        databaseModel.updateFilteredSongListFilter(regex, property);
    }

    @Override
    public void onValidateDatabase() {
        databaseModel.validateDatabase(getView()::setCheckerTextAreaText);
    }

    @Override
    public void onCompareDatabaseWithRemote() {
        Consumer<String> onDone = getView()::setCheckerTextAreaText;
        Consumer<Throwable> onThrow = this::defaultOnThrow;

        databaseModel.compareDatabaseWithRemote(onDone, onThrow);
    }

    @Override
    public void onLinkOcrTesterTable(TableView<OcrTestData> tableView) {
        SortedList<OcrTestData> list = new SortedList<>(ocrTestModel.getOcrTestDataList());
        list.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(list);
    }

    @Override
    public void onShowOcrTesterCacheDirectorySelector(Stage stage) {
        Path path = openDirectorySelector(stage);
        if (path != null) {
            getView().setOcrTesterCacheDirectoryText(path.toString());
        }
    }

    @Override
    public void onShowOcrTesterTessdataDirectorySelector(Stage stage) {
        Path path = openDirectorySelector(stage);
        if (path != null) {
            getView().setOcrTesterTessdataDirectoryText(path.toString());
        }
    }

    @Override
    public void onStartOcrTester(String cacheDirectory, String tessdataDirectory,
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

        if (!ocrTestModel.startOcrTestService(cachePath, tessdataPath, tessdataLanguage)) {
            defaultOnTaskRunning();
        }
    }

    @Override
    public void onStopOcrTester() {
        if (!ocrTestModel.stopOcrTestService()) {
            defaultOnTaskNotRunning();
        }
    }

    @Override
    protected HomePresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        Path path = openDirectorySelector(null);
        if (path == null) {
            return false;
        }

        try {
            databaseModel.load(path);
        } catch (IOException | RuntimeException e) {
            LOGGER.atError().log(EXCEPTION_LOG_MESSAGE, e);
            Dialogs.showException(e);
            return false;
        }

        return true;
    }

    @Override
    protected boolean terminate() {
        if (ServiceManager.getInstance().isRunningAny()) {
            Platform.runLater(
                    () -> Dialogs.showWarning("Some tasks are still running.", "Unable to exit."));
            return false;
        }

        return true;
    }
}
