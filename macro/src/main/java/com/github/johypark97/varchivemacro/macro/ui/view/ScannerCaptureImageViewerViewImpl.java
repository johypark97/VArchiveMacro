package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageDisplay;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerCaptureImageViewer;
import com.github.johypark97.varchivemacro.macro.ui.view.component.ImageViewerBox;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.CaptureImageViewerViewModel;
import java.io.IOException;
import java.net.URL;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ScannerCaptureImageViewerViewImpl extends BorderPane
        implements ScannerCaptureImageViewer.ScannerCaptureImageViewerView {
    private static final String FXML_PATH = "/fxml/ScannerCaptureImageViewer.fxml";
    private static final double TITLE_IMAGE_HEIGHT = 128;

    private final CellBox[][] cellBoxArray = new CellBox[4][4];
    private final ImageDisplay titleImageDisplay = new BlackImageDisplay();
    private final ImageViewerBox captureImageViewerBox = new ImageViewerBox();

    @FXML
    private TextField filterTextField;

    @FXML
    private Button filterResetButton;

    @FXML
    private ListView<CaptureImageViewerViewModel.CaptureImage> captureImageListView;

    @FXML
    private Tab captureImageTab;

    @FXML
    private VBox titleImageBox;

    @FXML
    private TextField titleTextTextField;

    @FXML
    private Label notAnalyzedLabel;

    @FXML
    private GridPane cellDataGridPane;

    @FXML
    private Button closeButton;

    @MvpPresenter
    public ScannerCaptureImageViewer.ScannerCaptureImageViewerPresenter presenter;

    public ScannerCaptureImageViewerViewImpl() {
        URL fxmlUrl = ScannerCaptureImageViewerViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        filterTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> presenter.updateFilter(newValue));

        filterResetButton.disableProperty()
                .bind(Bindings.equal(filterTextField.textProperty(), ""));

        filterResetButton.setOnAction(event -> {
            filterTextField.setText("");
            filterTextField.requestFocus();
        });

        captureImageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CaptureImageViewerViewModel.CaptureImage item,
                    boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(String.format("(%04d) %s", item.entryId(), item.scannedTitle()));
            }
        });

        captureImageListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        presenter.showCaptureImage(newValue.entryId());
                    }
                });

        captureImageTab.setContent(captureImageViewerBox);

        titleImageBox.getChildren().addFirst(titleImageDisplay);
        titleImageDisplay.setMinHeight(TITLE_IMAGE_HEIGHT / 4);
        titleImageDisplay.setPrefHeight(TITLE_IMAGE_HEIGHT);

        notAnalyzedLabel.setVisible(false);
        SimpleTransition transition = new SimpleTransition(Duration.seconds(1),
                x -> notAnalyzedLabel.setTextFill(new Color(x, 0, 0, 1)));
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                CellBox cellBox = new CellBox();
                cellBoxArray[row][column] = cellBox;
                cellDataGridPane.add(cellBox, column + 1, row + 1);
            }
        }

        closeButton.setOnAction(event -> presenter.requestStopStage());

        Platform.runLater(closeButton::requestFocus);
    }

    @Override
    public void setCaptureImageList(
            ObservableList<CaptureImageViewerViewModel.CaptureImage> value) {
        captureImageListView.setItems(value);
    }

    @Override
    public void showCaptureImage(CaptureImageViewerViewModel.CaptureImageDetail value) {
        captureImageViewerBox.setImage(value.captureImage);

        titleImageDisplay.setImage(value.titleImage);
        titleTextTextField.setText(value.titleText);

        notAnalyzedLabel.setVisible(!value.analyzed);

        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 4; column++) {
                CaptureImageViewerViewModel.CaptureImageDetail.CellData cellData =
                        value.cellDataArray[row][column];

                cellBoxArray[row][column].update(cellData.rateImage(), cellData.maxComboImage(),
                        cellData.rateText(), cellData.maxCombo());
            }
        }
    }

    public static class BlackImageDisplay extends ImageDisplay {
        public BlackImageDisplay() {
            setPrefHeight(0);
            setPrefWidth(0);
            setStyle("-fx-background-color: black;");
        }
    }


    public static class CellBox extends GridPane {
        private final CheckBox maxComboCheckBox = new CheckBox("MAX");
        private final ImageDisplay maxComboImageDisplay = new BlackImageDisplay();
        private final ImageDisplay rateImageDisplay = new BlackImageDisplay();
        private final TextField rateTextField = new TextField();

        private final ColumnConstraints[] columnArray = new ColumnConstraints[2];

        public CellBox() {
            setupGrid();

            rateTextField.setAlignment(Pos.CENTER);
            rateTextField.setEditable(false);
            rateTextField.setPrefColumnCount(0);

            maxComboCheckBox.setDisable(true);
            maxComboCheckBox.opacityProperty()
                    .bind(Bindings.when(maxComboCheckBox.selectedProperty().not()).then(0.5)
                            .otherwise(1));

            add(rateImageDisplay, 0, 0);
            add(maxComboImageDisplay, 1, 0);
            add(rateTextField, 0, 1);
            add(maxComboCheckBox, 1, 1);
        }

        public void update(Image rateImage, Image maxComboImage, String rateText,
                boolean maxCombo) {
            maxComboCheckBox.setSelected(maxCombo);
            maxComboImageDisplay.setImage(maxComboImage);
            rateImageDisplay.setImage(rateImage);
            rateTextField.setText(rateText);

            double leftImageWidthRatio =
                    rateImage.getWidth() / (rateImage.getWidth() + maxComboImage.getWidth());

            columnArray[0].setPercentWidth(leftImageWidthRatio * 100);
            columnArray[1].setPercentWidth((1 - leftImageWidthRatio) * 100);
        }

        private void setupGrid() {
            RowConstraints[] rowArray = new RowConstraints[2];

            rowArray[0] = new RowConstraints();
            rowArray[0].setValignment(VPos.CENTER);
            rowArray[0].setVgrow(Priority.ALWAYS);

            rowArray[1] = new RowConstraints();
            rowArray[1].setValignment(VPos.CENTER);
            getRowConstraints().addAll(rowArray);

            columnArray[0] = new ColumnConstraints();
            columnArray[0].setHalignment(HPos.CENTER);
            columnArray[0].setHgrow(Priority.ALWAYS);

            columnArray[1] = new ColumnConstraints();
            columnArray[1].setHalignment(HPos.CENTER);
            columnArray[1].setHgrow(Priority.ALWAYS);
            getColumnConstraints().addAll(columnArray);
        }
    }
}
