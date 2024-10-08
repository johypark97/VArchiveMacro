package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData.FoundData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongWrapper;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongWrapper.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.TitleComparator;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class HomeComponent extends TabPane {
    private static final String FXML_FILENAME = "Home.fxml";

    private final WeakReference<HomeView> viewReference;

    @FXML
    public TextField viewer_filterTextField;

    @FXML
    public ComboBox<SongDataProperty> viewer_filterComboBox;

    @FXML
    public Button viewer_filterResetButton;

    @FXML
    public TableView<SongWrapper> viewer_tableView;

    @FXML
    public TextArea checker_textArea;

    @FXML
    public Button checker_compareWithRemoteButton;

    @FXML
    public TextField ocrTester_cacheDirectoryTextField;

    @FXML
    public Button ocrTester_cacheDirectorySelectButton;

    @FXML
    public TextField ocrTester_tessdataDirectoryTextField;

    @FXML
    public Button ocrTester_tessdataDirectorySelectButton;

    @FXML
    public TextField ocrTester_tessdataLanguageTextField;

    @FXML
    public TableView<OcrTestData> ocrTester_tableView;

    @FXML
    public ProgressBar ocrTester_progressBar;

    @FXML
    public Label ocrTester_progressLabel;

    @FXML
    public Button ocrTester_startButton;

    @FXML
    public Button ocrTester_stopButton;

    @FXML
    public Slider ocrCacheCapturer_captureDelaySlider;

    @FXML
    public TextField ocrCacheCapturer_captureDelayTextField;

    @FXML
    public Slider ocrCacheCapturer_keyInputDelaySlider;

    @FXML
    public TextField ocrCacheCapturer_keyInputDelayTextField;

    @FXML
    public Slider ocrCacheCapturer_keyInputDurationSlider;

    @FXML
    public TextField ocrCacheCapturer_keyInputDurationTextField;

    @FXML
    public TextField ocrCacheCapturer_outputDirectoryTextField;

    @FXML
    public Button ocrCacheCapturer_outputDirectorySelectButton;

    @FXML
    public TextField ocrCacheClassifier_inputDirectoryTextField;

    @FXML
    public Button ocrCacheClassifier_inputDirectorySelectButton;

    @FXML
    public TextField ocrCacheClassifier_outputDirectoryTextField;

    @FXML
    public Button ocrCacheClassifier_outputDirectorySelectButton;

    @FXML
    public ProgressBar ocrCacheClassifier_progressBar;

    @FXML
    public Label ocrCacheClassifier_progressLabel;

    @FXML
    public Button ocrCacheClassifier_startButton;

    @FXML
    public Button ocrCacheClassifier_stopButton;

    @FXML
    public TextField ocrGroundTruthGenerator_inputDirectoryTextField;

    @FXML
    public Button ocrGroundTruthGenerator_inputDirectorySelectButton;

    @FXML
    public TextField ocrGroundTruthGenerator_outputDirectoryTextField;

    @FXML
    public Button ocrGroundTruthGenerator_outputDirectorySelectButton;

    @FXML
    public ProgressBar ocrGroundTruthGenerator_progressBar;

    @FXML
    public Label ocrGroundTruthGenerator_progressLabel;

    @FXML
    public Button ocrGroundTruthGenerator_startButton;

    @FXML
    public Button ocrGroundTruthGenerator_stopButton;

    @FXML
    public TextField liveTester_tessdataDirectoryTextField;

    @FXML
    public Button liveTester_tessdataDirectorySelectButton;

    @FXML
    public TextField liveTester_tessdataLanguageTextField;

    @FXML
    public Button liveTester_openButton;

    @FXML
    public Button liveTester_closeButton;

    public SliderTextFieldLinker ocrCacheCapturer_captureDelayLinker;
    public SliderTextFieldLinker ocrCacheCapturer_keyInputDelayLinker;
    public SliderTextFieldLinker ocrCacheCapturer_keyInputDurationLinker;

    public HomeComponent(HomeView view) {
        viewReference = new WeakReference<>(view);

        URL url = HomeComponent.class.getResource(FXML_FILENAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setupViewerTab();
        setupCheckerTab();
        setupOcrTesterTab();
        setupCacheCapturer();
        setupOcrClassifier();
        setupOcrGroundTruthGenerator();
        setupLiveTester();
    }

    private HomeView getView() {
        return viewReference.get();
    }

    private void setupViewerTab() {
        setupViewerTab_tableView();

        viewer_filterTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> getView().viewer_updateTableFilter());
        viewer_filterComboBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> getView().viewer_updateTableFilter());
        viewer_filterResetButton.setOnAction(event -> viewer_filterTextField.clear());
    }

    private void setupViewerTab_tableView() {
        TableColumn<SongWrapper, Integer> id = new TableColumn<>("Id");
        TableColumn<SongWrapper, String> title = new TableColumn<>("Title");
        TableColumn<SongWrapper, String> composer = new TableColumn<>("Composer");
        TableColumn<SongWrapper, String> pack = new TableColumn<>("Pack");
        TableColumn<SongWrapper, String> category = new TableColumn<>("Category");
        TableColumn<SongWrapper, String> priority = new TableColumn<>("Priority");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        composer.setCellValueFactory(new PropertyValueFactory<>("composer"));
        pack.setCellValueFactory(new PropertyValueFactory<>("pack"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));

        {
            TableColumn<SongWrapper, Integer> categoryPriority = new TableColumn<>("Category");
            TableColumn<SongWrapper, Integer> packPriority = new TableColumn<>("Pack");
            TableColumn<SongWrapper, Integer> titlePriority = new TableColumn<>("Title");

            categoryPriority.setCellValueFactory(new PropertyValueFactory<>("categoryPriority"));
            packPriority.setCellValueFactory(new PropertyValueFactory<>("packPriority"));
            titlePriority.setCellValueFactory(new PropertyValueFactory<>("titlePriority"));

            priority.getColumns().setAll(List.of(categoryPriority, packPriority, titlePriority));
        }

        title.setComparator(new TitleComparator());

        viewer_tableView.getColumns()
                .setAll(List.of(id, title, composer, pack, category, priority));

        viewer_tableView.getSortOrder().setAll(List.of(title));
    }

    private void setupCheckerTab() {
        checker_compareWithRemoteButton.setOnAction(
                event -> getView().checker_compareDatabaseWithRemote());
    }

    private void setupOcrTesterTab() {
        setupOcrTesterTab_tableView();

        ocrTester_cacheDirectorySelectButton.setOnAction(
                event -> getView().ocrTester_selectCacheDirectory());
        ocrTester_tessdataDirectorySelectButton.setOnAction(
                event -> getView().ocrTester_selectTessdataDirectory());
        ocrTester_startButton.setOnAction(event -> getView().ocrTester_start());
        ocrTester_stopButton.setOnAction(event -> getView().ocrTester_stop());
    }

    private void setupOcrTesterTab_tableView() {
        TableColumn<OcrTestData, ?> target = new TableColumn<>("Target");
        {
            TableColumn<OcrTestData, Integer> id = new TableColumn<>("Id");
            TableColumn<OcrTestData, String> title = new TableColumn<>("Title");
            TableColumn<OcrTestData, String> composer = new TableColumn<>("Composer");

            id.setCellValueFactory(new PropertyValueFactory<>("targetId"));
            title.setCellValueFactory(new PropertyValueFactory<>("targetTitle"));
            composer.setCellValueFactory(new PropertyValueFactory<>("targetComposer"));

            title.setComparator(new TitleComparator());

            target.getColumns().setAll(List.of(id, title, composer));
        }

        TableColumn<OcrTestData, String> find = new TableColumn<>("Find");
        {
            TableColumn<OcrTestData, String> normalizedTitle =
                    new TableColumn<>("Normalized Title");
            normalizedTitle.setCellValueFactory(
                    new PropertyValueFactory<>("targetNormalizedTitle"));
            normalizedTitle.setComparator(new TitleComparator());

            TableColumn<OcrTestData, String> scannedTitle = new TableColumn<>("Scanned Title");
            scannedTitle.setCellValueFactory(new PropertyValueFactory<>("scannedTitle"));
            scannedTitle.setComparator(new TitleComparator());

            TableColumn<OcrTestData, List<String>> foundKeyList = new TableColumn<>("Found Keys");
            foundKeyList.setCellValueFactory(new PropertyValueFactory<>("foundKeyList"));

            find.getColumns().setAll(List.of(normalizedTitle, scannedTitle, foundKeyList));
        }

        TableColumn<OcrTestData, List<FoundData>> recognized = new TableColumn<>("Recognized");
        recognized.setCellValueFactory(new PropertyValueFactory<>("foundDataList"));
        recognized.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(List<FoundData> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item.stream().map(x -> String.format("[%d, %.2f%%] %s - %s", x.distance,
                                x.accuracy * 100, x.song.title(), x.song.composer()))
                        .collect(Collectors.joining(", ")));
            }
        });

        TableColumn<OcrTestData, String> test = new TableColumn<>("Test");
        {
            TableColumn<OcrTestData, String> status = new TableColumn<>("Status");
            TableColumn<OcrTestData, Boolean> pass = new TableColumn<>("Pass");

            status.setCellValueFactory(new PropertyValueFactory<>("testStatus"));
            pass.setCellValueFactory(new PropertyValueFactory<>("testPass"));

            pass.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                        return;
                    }

                    CheckBox checkBox = new CheckBox();
                    checkBox.setDisable(true);
                    checkBox.setOpacity(1);
                    checkBox.setSelected(item);

                    setAlignment(Pos.CENTER);
                    setGraphic(checkBox);
                }
            });

            test.getColumns().setAll(List.of(status, pass));
        }

        ocrTester_tableView.getColumns().setAll(List.of(target, find, recognized, test));

        ocrTester_tableView.setRowFactory(param -> new TableRow<>() {
            private static final String STYLE_CLASS_EXACT = "table-row-color-green";
            private static final String STYLE_CLASS_SIMILAR = "table-row-color-yellow";
            private static final String STYLE_CLASS_WRONG = "table-row-color-red";

            @Override
            protected void updateItem(OcrTestData item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(STYLE_CLASS_EXACT, STYLE_CLASS_SIMILAR,
                        STYLE_CLASS_WRONG);

                if (empty || item == null) {
                    return;
                }

                switch (item.testStatus) {
                    case EXACT, DUPLICATED_EXACT -> getStyleClass().add(STYLE_CLASS_EXACT);
                    case SIMILAR, DUPLICATED_SIMILAR -> getStyleClass().add(STYLE_CLASS_SIMILAR);
                    case WRONG -> getStyleClass().add(STYLE_CLASS_WRONG);
                    default -> {
                    }
                }
            }
        });
    }

    private void setupCacheCapturer() {
        ocrCacheCapturer_captureDelayLinker =
                new SliderTextFieldLinker(ocrCacheCapturer_captureDelaySlider,
                        ocrCacheCapturer_captureDelayTextField);

        ocrCacheCapturer_keyInputDelayLinker =
                new SliderTextFieldLinker(ocrCacheCapturer_keyInputDelaySlider,
                        ocrCacheCapturer_keyInputDelayTextField);

        ocrCacheCapturer_keyInputDurationLinker =
                new SliderTextFieldLinker(ocrCacheCapturer_keyInputDurationSlider,
                        ocrCacheCapturer_keyInputDurationTextField);

        ocrCacheCapturer_outputDirectorySelectButton.setOnAction(
                event -> getView().ocrCacheCapturer_selectOutputDirectory());
    }

    private void setupOcrClassifier() {
        ocrCacheClassifier_inputDirectorySelectButton.setOnAction(
                event -> getView().ocrCacheClassifier_selectInputDirectory());

        ocrCacheClassifier_outputDirectorySelectButton.setOnAction(
                event -> getView().ocrCacheClassifier_selectOutputDirectory());

        ocrCacheClassifier_startButton.setOnAction(event -> getView().ocrCacheClassifier_start());

        ocrCacheClassifier_stopButton.setOnAction(event -> getView().ocrCacheClassifier_stop());
    }

    private void setupOcrGroundTruthGenerator() {
        ocrGroundTruthGenerator_inputDirectorySelectButton.setOnAction(
                event -> getView().ocrGroundTruthGenerator_selectInputDirectory());

        ocrGroundTruthGenerator_outputDirectorySelectButton.setOnAction(
                event -> getView().ocrGroundTruthGenerator_selectOutputDirectory());

        ocrGroundTruthGenerator_startButton.setOnAction(
                event -> getView().ocrGroundTruthGenerator_start());

        ocrGroundTruthGenerator_stopButton.setOnAction(
                event -> getView().ocrGroundTruthGenerator_stop());
    }

    private void setupLiveTester() {
        liveTester_tessdataDirectorySelectButton.setOnAction(
                event -> getView().liveTester_selectTessdataDirectory());

        liveTester_openButton.setOnAction(event -> getView().liveTester_open());

        liveTester_closeButton.setOnAction(event -> getView().liveTester_close());
    }
}
