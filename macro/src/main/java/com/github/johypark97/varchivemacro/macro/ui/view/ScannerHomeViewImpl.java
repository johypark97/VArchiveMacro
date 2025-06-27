package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerHome;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerHomeViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ScannerHomeViewImpl extends BorderPane implements ScannerHome.ScannerHomeView {
    private static final String FXML_PATH = "/fxml/ScannerHome.fxml";

    @FXML
    private SplitPane recordViewerSplitPane;

    @FXML
    private TextField recordViewerSongFilterTextField;

    @FXML
    private Button recordViewerSongFilterResetButton;

    @FXML
    private TreeView<ScannerHomeViewModel.SongTreeViewData> recordViewerSongTreeView;

    @FXML
    private Button recordViewerReloadRecordButton;

    @FXML
    private TextArea recordViewerSongInformationTextArea;

    @FXML
    private GridPane recordViewerSongRecordGridPane;

    @FXML
    private Button recordViewerScanButton;

    @FXML
    private VBox recordLoaderBox;

    @FXML
    private TextField recordLoaderDjNameTextField;

    @FXML
    private TextField recordLoaderAccountFileTextField;

    @FXML
    private Tooltip recordLoaderAccountFileTextFieldTooltip;

    @FXML
    private Button recordLoaderAccountFileSelectButton;

    @FXML
    private Button recordLoaderLoadButton;

    @FXML
    private Button recordLoaderCancelButton;

    @FXML
    private VBox progressBox;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Group unavailableGroup;

    @FXML
    private Label progressLabel;

    @FXML
    private Button homeButton;

    private RecordGridController recordGridController;

    @MvpPresenter
    public ScannerHome.ScannerHomePresenter presenter;

    public ScannerHomeViewImpl() {
        URL fxmlUrl = ScannerHomeViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        setupViewer();
        setupLoader();

        homeButton.setOnAction(event -> presenter.showHome());
    }

    private void setupViewer() {
        recordViewerSongFilterTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> presenter.updateSongFilter(newValue));

        recordViewerSongFilterResetButton.disableProperty()
                .bind(Bindings.equal(recordViewerSongFilterTextField.textProperty(), ""));

        recordViewerSongFilterResetButton.setOnAction(event -> {
            recordViewerSongFilterTextField.setText("");
            recordViewerSongFilterTextField.requestFocus();
        });

        recordViewerSongTreeView.setShowRoot(false);

        recordViewerSongTreeView.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(ScannerHomeViewModel.SongTreeViewData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                if (item.songId() == -1) {
                    setText(item.category());
                    return;
                }

                setText(String.format("%s ...... %s", item.title(), item.composer()));
            }
        });

        recordViewerSongTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        return;
                    }

                    ScannerHomeViewModel.SongTreeViewData data = newValue.getValue();
                    if (data.composer() == null) {
                        return;
                    }

                    presenter.showSong(data.songId());
                });

        recordViewerReloadRecordButton.setOnAction(event -> {
            recordLoaderCancelButton.setDisable(false);
            presenter.showRecordLoader();
        });

        recordGridController = new RecordGridController(recordViewerSongRecordGridPane);
        recordGridController.setupTransposeButton();

        recordViewerScanButton.setOnAction(event -> presenter.showScannerWindow());
    }

    private void setupLoader() {
        recordLoaderAccountFileTextFieldTooltip.textProperty()
                .bind(recordLoaderAccountFileTextField.textProperty());

        recordLoaderAccountFileSelectButton.setOnAction(
                event -> presenter.showAccountFileSelector());

        recordLoaderLoadButton.setOnAction(event -> presenter.loadRemoteRecord());

        recordLoaderCancelButton.setDisable(true);
        recordLoaderCancelButton.setOnAction(event -> showViewer());
    }

    @Override
    public void showViewer() {
        recordViewerSplitPane.setVisible(true);
        recordLoaderBox.setVisible(false);
        progressBox.setVisible(false);

        Platform.runLater(recordViewerScanButton::requestFocus);
    }

    @Override
    public void showLoader() {
        recordViewerSplitPane.setVisible(false);
        recordLoaderBox.setVisible(true);
        progressBox.setVisible(false);
    }

    @Override
    public void showProgress(String text) {
        recordViewerSplitPane.setVisible(false);
        recordLoaderBox.setVisible(false);
        progressBox.setVisible(true);

        progressIndicator.setVisible(true);
        unavailableGroup.setVisible(false);

        progressLabel.setText(text);
    }

    @Override
    public void showUnavailable(String text) {
        recordViewerSplitPane.setVisible(false);
        recordLoaderBox.setVisible(false);
        progressBox.setVisible(true);

        progressIndicator.setVisible(false);
        unavailableGroup.setVisible(true);

        progressLabel.setText(text);
    }

    @Override
    public String getDjNameText() {
        return recordLoaderDjNameTextField.getText();
    }

    @Override
    public String getAccountFileText() {
        return recordLoaderAccountFileTextField.getText();
    }

    @Override
    public void setAccountFileText(String value) {
        recordLoaderAccountFileTextField.setText(value);
    }

    @Override
    public void setSongTreeViewRoot(TreeItem<ScannerHomeViewModel.SongTreeViewData> value) {
        recordViewerSongTreeView.setRoot(value);
    }

    @Override
    public void showSongInformation(String title, String composer) {
        recordViewerSongInformationTextArea.setText(
                String.format("Title: %s\nComposer: %s", title, composer));
    }

    @Override
    public void showSongRecord(ScannerHomeViewModel.SongRecord value) {
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                recordGridController.clearCell(row, column);

                float rate = value.rate[row][column];
                if (rate == -1) {
                    recordGridController.shadowCell(row, column);
                } else {
                    boolean maxCombo = value.maxCombo[row][column];
                    recordGridController.setCell(row, column, rate, maxCombo);
                }
            }
        }
    }


    public static class RecordGridController {
        private final GridPane gridPane;

        public RecordGridController(GridPane gridPane) {
            this.gridPane = gridPane;
        }

        public void setupTransposeButton() {
            getTransposeButton().setOnAction(event -> gridPane.getChildren().forEach(x -> {
                int column = Objects.requireNonNullElse(GridPane.getColumnIndex(x), 0);
                int row = Objects.requireNonNullElse(GridPane.getRowIndex(x), 0);

                if (row != column) {
                    GridPane.setColumnIndex(x, row);
                    GridPane.setRowIndex(x, column);
                }
            }));
        }

        public void clearCell(int row, int column) {
            TextField textField = getTextField(row, column);

            textField.clear();
            textField.setDisable(false);
            textField.setEffect(null);
            textField.setStyle("");
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

        private Button getTransposeButton() {
            return (Button) gridPane.getChildren().getFirst();
        }

        private TextField getTextField(int row, int column) {
            if (row < 0 || row > 3 || column < 0 || column > 3) {
                throw new IllegalArgumentException();
            }

            return (TextField) gridPane.getChildren().get((row + 1) * 5 + column + 1);
        }
    }
}
