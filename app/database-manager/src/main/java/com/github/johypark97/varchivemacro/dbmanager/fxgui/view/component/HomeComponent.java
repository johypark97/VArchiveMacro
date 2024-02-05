package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.lib.common.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpFxml;
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

    public final HomeViewImpl view;

    @FXML
    public TextField viewerFilterTextField;

    @FXML
    public ComboBox<SongDataProperty> viewerFilterComboBox;

    @FXML
    public Button viewerFilterResetButton;

    @FXML
    public TableView<SongData> viewerTableView;

    @FXML
    public TextArea checkerTextArea;

    @FXML
    public Button checkerValidateButton;

    @FXML
    public Button checkerCompareWithRemoteButton;

    @FXML
    public TextField ocrTesterCacheDirectoryTextField;

    @FXML
    public Button ocrTesterCacheDirectorySelectButton;

    @FXML
    public TextField ocrTesterTessdataDirectoryTextField;

    @FXML
    public Button ocrTesterTessdataDirectorySelectButton;

    @FXML
    public TextField ocrTesterTessdataLanguageTextField;

    @FXML
    public TableView<OcrTestData> ocrTesterTableView;

    @FXML
    public ProgressBar ocrTesterProgressBar;

    @FXML
    public Label ocrTesterProgressLabel;

    @FXML
    public Button ocrTesterStartButton;

    @FXML
    public Button ocrTesterStopButton;

    @FXML
    public Slider ocrCacheCapturerCaptureDelaySlider;

    @FXML
    public TextField ocrCacheCapturerCaptureDelayTextField;

    @FXML
    public Slider ocrCacheCapturerKeyInputDelaySlider;

    @FXML
    public TextField ocrCacheCapturerKeyInputDelayTextField;

    @FXML
    public Slider ocrCacheCapturerKeyInputDurationSlider;

    @FXML
    public TextField ocrCacheCapturerKeyInputDurationTextField;

    @FXML
    public TextField ocrCacheCapturerOutputDirectoryTextField;

    @FXML
    public Button ocrCacheCapturerOutputDirectorySelectButton;

    public SliderTextFieldLinker ocrCacheCapturerCaptureDelayLinker;
    public SliderTextFieldLinker ocrCacheCapturerKeyInputDelayLinker;
    public SliderTextFieldLinker ocrCacheCapturerKeyInputDurationLinker;

    public HomeComponent(HomeViewImpl view) {
        this.view = view;

        URL url = HomeComponent.class.getResource(FXML_FILENAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setupViewerTab();
        setupCheckerTab();
        setupOcrTesterTab();
        setupCacheCapturer();
    }

    private void setupViewerTab() {
        setupViewerTab_tableView();

        viewerFilterTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> view.updateViewerTableFilter());
        viewerFilterComboBox.valueProperty()
                .addListener((observable, oldValue, newValue) -> view.updateViewerTableFilter());
        viewerFilterResetButton.setOnAction(event -> viewerFilterTextField.clear());
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

        viewerTableView.getColumns()
                .setAll(List.of(id, title, remoteTitle, composer, dlc, priority));

        viewerTableView.getSortOrder().setAll(List.of(title, priority));
    }

    private void setupCheckerTab() {
        checkerValidateButton.setOnAction(event -> view.validateDatabase());
        checkerCompareWithRemoteButton.setOnAction(event -> view.compareDatabaseWithRemote());
    }

    private void setupOcrTesterTab() {
        setupOcrTesterTab_tableView();

        ocrTesterCacheDirectorySelectButton.setOnAction(
                event -> view.showOcrTesterCacheDirectorySelector());
        ocrTesterTessdataDirectorySelectButton.setOnAction(
                event -> view.showOcrTesterTessdataDirectorySelector());
        ocrTesterStartButton.setOnAction(event -> view.startOcrTester());
        ocrTesterStopButton.setOnAction(event -> view.stopOcrTester());
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

        ocrTesterTableView.getColumns().setAll(List.of(target, match, recognized, test));

        ocrTesterTableView.setRowFactory(param -> new TableRow<>() {
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
        ocrCacheCapturerCaptureDelayLinker =
                new SliderTextFieldLinker(ocrCacheCapturerCaptureDelaySlider,
                        ocrCacheCapturerCaptureDelayTextField);

        ocrCacheCapturerKeyInputDelayLinker =
                new SliderTextFieldLinker(ocrCacheCapturerKeyInputDelaySlider,
                        ocrCacheCapturerKeyInputDelayTextField);

        ocrCacheCapturerKeyInputDurationLinker =
                new SliderTextFieldLinker(ocrCacheCapturerKeyInputDurationSlider,
                        ocrCacheCapturerKeyInputDurationTextField);

        ocrCacheCapturerOutputDirectorySelectButton.setOnAction(
                event -> view.showOcrCacheCapturerOutputDirectorySelector());
    }
}
