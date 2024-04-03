package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ScannerComponent extends TabPane {
    private static final String FXML_FILE_NAME = "Scanner.fxml";

    private final WeakReference<HomeView> viewReference;

    @FXML
    public TextField viewer_filterTextField;

    @FXML
    public Button viewer_filterResetButton;

    @FXML
    public TreeView<ViewerTreeData> viewer_songTreeView;

    @FXML
    public TextArea viewer_informationTextArea;

    @FXML
    public GridPane viewer_recordGridPane;

    @FXML
    public ListView<ScannerTabListData> scanner_tabListView;

    @FXML
    public Button scanner_selectAllTabButton;

    @FXML
    public Button scanner_unselectAllTabButton;

    @FXML
    public TextField option_cacheDirectoryTextField;

    @FXML
    public Button option_cacheDirectorySelectButton;

    @FXML
    public Slider option_captureDelaySlider;

    @FXML
    public TextField option_captureDelayTextField;

    @FXML
    public Slider option_keyInputDurationSlider;

    @FXML
    public TextField option_keyInputDurationTextField;

    @FXML
    public TextField option_accountFileTextField;

    @FXML
    public Button option_accountFileSelectButton;

    @FXML
    public Slider option_recordUploadDelaySlider;

    @FXML
    public TextField option_recordUploadDelayTextField;

    public SliderTextFieldLinker optionCaptureDelayLinker;
    public SliderTextFieldLinker optionKeyInputDurationLinker;
    public SliderTextFieldLinker optionRecordUploadDelayLinker;

    private ViewerRecordController viewerRecordController;

    public ScannerComponent(HomeView view) {
        viewReference = new WeakReference<>(view);

        URL url = ScannerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setVisible(false);

        setupViewer();
        setupScanner();
        setupOption();
    }

    public void viewer_showInformation(String title, String composer) {
        StringBuilder builder = new StringBuilder();

        builder.append("Title: ").append(title).append(System.lineSeparator());
        builder.append("Composer: ").append(composer);

        viewer_informationTextArea.setText(builder.toString());
    }

    public void viewer_resetRecord(int row, int column) {
        viewerRecordController.clearCell(row, column);
    }

    public void viewer_setRecord(int row, int column, float rate, boolean maxCombo) {
        viewerRecordController.setCell(row, column, rate, maxCombo);
    }

    public void viewer_shadowRecord(int row, int column) {
        viewerRecordController.shadowCell(row, column);
    }

    public void scanner_setTabList(List<String> list) {
        ObservableList<ScannerTabListData> observableList =
                list.stream().map(ScannerTabListData::new)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

        scanner_tabListView.setItems(observableList);
    }

    public Set<String> scanner_getSelectedTabSet() {
        return scanner_tabListView.getItems().stream().filter(x -> x.checked.get()).map(x -> x.name)
                .collect(Collectors.toSet());
    }

    public void scanner_setSelectedTabSet(Set<String> value) {
        scanner_tabListView.getItems().forEach(x -> x.checked.setValue(value.contains(x.name)));
    }

    public String option_getCacheDirectory() {
        return option_cacheDirectoryTextField.getText();
    }

    public void option_setCacheDirectory(String value) {
        option_cacheDirectoryTextField.setText(value);
    }

    public int option_getCaptureDelay() {
        return optionCaptureDelayLinker.getValue();
    }

    public int option_getKeyInputDuration() {
        return optionKeyInputDurationLinker.getValue();
    }

    public String option_getAccountFile() {
        return option_accountFileTextField.getText();
    }

    public void option_setAccountFile(String value) {
        option_accountFileTextField.setText(value);
    }

    public int option_getRecordUploadDelay() {
        return optionRecordUploadDelayLinker.getValue();
    }

    private HomeView getView() {
        return viewReference.get();
    }

    private void setupViewer() {
        viewer_filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if (!value.equals(oldValue.trim())) {
                getView().scanner_viewer_showSongTree(newValue.trim());
            }
        });

        viewer_filterResetButton.setOnAction(event -> viewer_filterTextField.setText(""));

        setupViewer_treeView();

        viewerRecordController = new ViewerRecordController(viewer_recordGridPane);
        viewerRecordController.setupTransposeButton();
    }

    private void setupViewer_treeView() {
        viewer_songTreeView.setShowRoot(false);

        viewer_songTreeView.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(ViewerTreeData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                if (item.name != null) {
                    setText(item.name);
                    return;
                }

                setText(String.format("%s ...... %s", item.song.title, item.song.composer));
            }
        });

        viewer_songTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        return;
                    }

                    ViewerTreeData data = newValue.getValue();
                    if (data.song == null) {
                        return;
                    }

                    getView().scanner_viewer_showRecord(data.song.id);
                });
    }

    private void setupScanner() {
        scanner_tabListView.setCellFactory(CheckBoxListCell.forListView(param -> param.checked));

        scanner_selectAllTabButton.setOnAction(
                event -> scanner_tabListView.getItems().forEach(x -> x.checked.setValue(true)));

        scanner_unselectAllTabButton.setOnAction(
                event -> scanner_tabListView.getItems().forEach(x -> x.checked.setValue(false)));
    }

    private void setupOption() {
        option_cacheDirectorySelectButton.setOnAction(
                event -> getView().scanner_option_openCacheDirectorySelector());

        optionCaptureDelayLinker =
                new SliderTextFieldLinker(option_captureDelaySlider, option_captureDelayTextField);

        optionKeyInputDurationLinker = new SliderTextFieldLinker(option_keyInputDurationSlider,
                option_keyInputDurationTextField);

        option_accountFileSelectButton.setOnAction(
                event -> getView().scanner_option_openAccountFileSelector());

        optionRecordUploadDelayLinker = new SliderTextFieldLinker(option_recordUploadDelaySlider,
                option_recordUploadDelayTextField);
    }

    public static class ViewerRecordController {
        private final WeakReference<GridPane> gridPaneReference;

        public ViewerRecordController(GridPane gridPane) {
            gridPaneReference = new WeakReference<>(gridPane);
        }

        public void setupTransposeButton() {
            getTransposeButton().setOnAction(event -> {
                int count = getGridPane().getChildren().size();
                for (int i = 0; i < count; ++i) {
                    Node node = getGridPane().getChildren().get(i);

                    int column = Objects.requireNonNullElse(GridPane.getColumnIndex(node), 0);
                    int row = Objects.requireNonNullElse(GridPane.getRowIndex(node), 0);

                    if (row != count) {
                        GridPane.setColumnIndex(node, row);
                        GridPane.setRowIndex(node, column);
                    }
                }
            });
        }

        public void clearCell(int row, int column) {
            TextField textField = getTextField(row, column);

            textField.setDisable(false);
            textField.setEffect(null);
            textField.setStyle("");
            textField.setText("");
        }

        public void setCell(int row, int column, float rate, boolean maxCombo) {
            TextField textField = getTextField(row, column);
            if (rate == 0) {
                textField.setText("-");
                return;
            }

            textField.setText(String.format("%.2f", rate));

            if (rate == 100) { // NOPMD
                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.RED);

                textField.setEffect(shadow);
                textField.setStyle("-fx-background-color: #FFF0F0;");
            } else if (maxCombo) {
                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.LIME);

                textField.setEffect(shadow);
                textField.setStyle("-fx-background-color: #F0FFF0;");
            }
        }

        public void shadowCell(int row, int column) {
            getTextField(row, column).setDisable(true);
        }

        private GridPane getGridPane() {
            return gridPaneReference.get();
        }

        private Button getTransposeButton() {
            return (Button) getGridPane().getChildren().get(0);
        }

        private TextField getTextField(int row, int column) {
            if (row < 0 || row > 3 || column < 0 || column > 3) {
                throw new IllegalArgumentException();
            }

            return (TextField) getGridPane().getChildren().get((row + 1) * 5 + column + 1);
        }
    }


    public static class ScannerTabListData {
        private final BooleanProperty checked = new SimpleBooleanProperty();
        private final String name;

        public ScannerTabListData(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
