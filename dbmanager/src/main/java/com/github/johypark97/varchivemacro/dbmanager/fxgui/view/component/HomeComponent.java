package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.TitleComparator;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
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
    public TableView<SongData> viewer_tableView;

    @FXML
    public TextArea checker_textArea;

    @FXML
    public Button checker_validateButton;

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
        TableColumn<SongData, Integer> id = new TableColumn<>("Id");
        TableColumn<SongData, String> title = new TableColumn<>("Title");
        TableColumn<SongData, String> remoteTitle = new TableColumn<>("Remote Title");
        TableColumn<SongData, String> composer = new TableColumn<>("Composer");
        TableColumn<SongData, String> dlc = new TableColumn<>("Dlc");
        TableColumn<SongData, Integer> priority = new TableColumn<>("Priority");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        remoteTitle.setCellValueFactory(new PropertyValueFactory<>("remoteTitle"));
        composer.setCellValueFactory(new PropertyValueFactory<>("composer"));
        dlc.setCellValueFactory(new PropertyValueFactory<>("dlc"));
        priority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        title.setComparator(new TitleComparator());
        remoteTitle.setComparator(new TitleComparator());

        viewer_tableView.getColumns()
                .setAll(List.of(id, title, remoteTitle, composer, dlc, priority));

        viewer_tableView.getSortOrder().setAll(List.of(title, priority));
    }

    private void setupCheckerTab() {
        checker_validateButton.setOnAction(event -> getView().checker_validateDatabase());
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

        TableColumn<OcrTestData, String> match = new TableColumn<>("Match");
        {
            TableColumn<OcrTestData, String> normalizedTitle =
                    new TableColumn<>("Normalized Title");
            TableColumn<OcrTestData, String> scannedTitle = new TableColumn<>("Scanned Title");
            TableColumn<OcrTestData, String> foundKey = new TableColumn<>("Found Key");

            normalizedTitle.setCellValueFactory(new PropertyValueFactory<>("matchNormalizedTitle"));
            scannedTitle.setCellValueFactory(new PropertyValueFactory<>("matchScannedTitle"));
            foundKey.setCellValueFactory(new PropertyValueFactory<>("matchFoundKey"));

            normalizedTitle.setComparator(new TitleComparator());
            scannedTitle.setComparator(new TitleComparator());
            foundKey.setComparator(new TitleComparator());

            match.getColumns().setAll(List.of(normalizedTitle, scannedTitle, foundKey));
        }

        TableColumn<OcrTestData, String> recognized = new TableColumn<>("Recognized");
        {
            TableColumn<OcrTestData, Integer> id = new TableColumn<>("Id");
            TableColumn<OcrTestData, String> title = new TableColumn<>("Title");
            TableColumn<OcrTestData, String> composer = new TableColumn<>("Composer");

            id.setCellValueFactory(new PropertyValueFactory<>("recognizedId"));
            title.setCellValueFactory(new PropertyValueFactory<>("recognizedTitle"));
            composer.setCellValueFactory(new PropertyValueFactory<>("recognizedComposer"));

            title.setComparator(new TitleComparator());

            recognized.getColumns().setAll(List.of(id, title, composer));
        }

        TableColumn<OcrTestData, String> test = new TableColumn<>("Test");
        {
            TableColumn<OcrTestData, Integer> distance = new TableColumn<>("Distance");
            TableColumn<OcrTestData, Double> accuracy = new TableColumn<>("Accuracy");
            TableColumn<OcrTestData, String> status = new TableColumn<>("Status");
            TableColumn<OcrTestData, Boolean> pass = new TableColumn<>("Pass");

            distance.setCellValueFactory(new PropertyValueFactory<>("testDistance"));
            accuracy.setCellValueFactory(new PropertyValueFactory<>("testAccuracy"));
            status.setCellValueFactory(new PropertyValueFactory<>("testStatus"));
            pass.setCellValueFactory(new PropertyValueFactory<>("testPass"));

            accuracy.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(String.format("%.2f%%", item * 100));
                }
            });

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

            test.getColumns().setAll(List.of(distance, accuracy, status, pass));
        }

        ocrTester_tableView.getColumns().setAll(List.of(target, match, recognized, test));

        ocrTester_tableView.setRowFactory(param -> new TableRow<>() {
            private static final String STYLE_CLASS_EXACT = "table-row-color-green";
            private static final String STYLE_CLASS_SIMILAR = "table-row-color-yellow";

            @Override
            protected void updateItem(OcrTestData item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(STYLE_CLASS_EXACT, STYLE_CLASS_SIMILAR);

                if (empty || item == null) {
                    return;
                }

                if (item.isTestPass()) {
                    getStyleClass().add(
                            (item.testDistance == 0) ? STYLE_CLASS_EXACT : STYLE_CLASS_SIMILAR);
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
