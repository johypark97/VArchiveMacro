package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewer.RecordBoxData;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class AnalysisDataViewerComponent extends VBox {
    private static final String FXML_FILE_NAME = "AnalysisDataViewer.fxml";

    private static final AtomicBoolean transposeRecord = new AtomicBoolean();

    @FXML
    public ImageView titleImageView;

    @FXML
    public TextField titleTextField;

    @FXML
    public GridPane recordGridPane;

    @FXML
    public Button closeButton;

    private final List<RecordBox> recordBoxList = new ArrayList<>(16);

    public AnalysisDataViewerComponent() {
        URL url = AnalysisDataViewerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
    }

    public void setCloseButtonAction(Runnable runnable) {
        closeButton.setOnAction(event -> runnable.run());
    }

    @FXML
    public void initialize() {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                RecordBox box = new RecordBox();
                recordBoxList.add(box);
                recordGridPane.add(box, i + 1, j + 1);
            }
        }

        getTransposeButton().setOnAction(event -> {
            transposeRecordGridPane();
            toggleTransposeRecord();
        });

        if (transposeRecord.get()) {
            transposeRecordGridPane();
        }
    }

    public void setTitleImage(Image image) {
        titleImageView.setImage(image);
    }

    public void setTitleText(String text) {
        titleTextField.setText(text);
    }

    public void setRecordBoxData(int row, int column, RecordBoxData data) {
        RecordBox recordBox = recordBoxList.get(row * 4 + column);

        recordBox.maxComboCheckBox.setSelected(data.maxCombo);
        recordBox.maxComboImageView.setImage(data.maxComboImage);
        recordBox.rateImageView.setImage(data.rateImage);
        recordBox.rateTextField.setText(data.rateText);
    }

    private void transposeRecordGridPane() {
        recordGridPane.getChildren().forEach(x -> {
            int column = Objects.requireNonNullElse(GridPane.getColumnIndex(x), 0);
            int row = Objects.requireNonNullElse(GridPane.getRowIndex(x), 0);

            if (row != column) {
                GridPane.setColumnIndex(x, row);
                GridPane.setRowIndex(x, column);
            }
        });
    }

    private Button getTransposeButton() {
        return (Button) recordGridPane.getChildren().get(0);
    }

    private void toggleTransposeRecord() {
        boolean x;
        do {
            x = transposeRecord.get();
        } while (!transposeRecord.compareAndSet(x, !x));
    }

    public static class RecordBox extends GridPane {
        private final CheckBox maxComboCheckBox = new CheckBox();
        private final ImageView maxComboImageView = new ImageView();
        private final ImageView rateImageView = new ImageView();
        private final TextField rateTextField = new TextField();

        public RecordBox() {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.CENTER);
            rowConstraints.setVgrow(Priority.ALWAYS);
            getRowConstraints().add(rowConstraints);
            getRowConstraints().add(rowConstraints);

            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHalignment(HPos.CENTER);
            columnConstraints.setHgrow(Priority.ALWAYS);
            getColumnConstraints().add(columnConstraints);
            getColumnConstraints().add(columnConstraints);

            add(wrapWithBox(rateImageView), 0, 0);
            add(wrapWithBox(maxComboImageView), 1, 0);
            add(rateTextField, 0, 1);
            add(maxComboCheckBox, 1, 1);

            rateTextField.setAlignment(Pos.CENTER);
            rateTextField.setEditable(false);
            rateTextField.setPrefColumnCount(0);

            maxComboCheckBox.setDisable(true);
            maxComboCheckBox.setOpacity(1);
        }

        private Node wrapWithBox(Node node) {
            VBox box = new VBox(node);

            box.setAlignment(Pos.CENTER);
            box.setStyle("-fx-background-color: black;");

            return box;
        }
    }
}
