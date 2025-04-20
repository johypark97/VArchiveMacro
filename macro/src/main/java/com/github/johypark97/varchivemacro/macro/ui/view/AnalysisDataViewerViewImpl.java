package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.AnalysisDataViewerPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.AnalysisDataViewerView;
import com.github.johypark97.varchivemacro.macro.ui.presenter.AnalysisDataViewer.RecordBoxData;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import java.awt.Toolkit;
import java.io.IOException;
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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class AnalysisDataViewerViewImpl extends VBox implements AnalysisDataViewerView {
    private static final String FXML_PATH = "/fxml/AnalysisDataViewer.fxml";

    private static final AtomicBoolean transposeRecord = new AtomicBoolean();

    private final List<RecordBox> recordBoxList = new ArrayList<>(16);

    private final Stage stage;

    @MvpPresenter
    public AnalysisDataViewerPresenter presenter;

    @FXML
    public TextField songTextField;

    @FXML
    public ImageView titleImageView;

    @FXML
    public TextField titleTextField;

    @FXML
    public GridPane recordGridPane;

    @FXML
    public Button closeButton;

    public AnalysisDataViewerViewImpl(Stage stage) {
        this.stage = stage;

        URL fxmlUrl = AnalysisDataViewerViewImpl.class.getResource(FXML_PATH);
        URL globalCss = UiResource.getGlobalCss();
        URL tableColorCss = UiResource.getTableColorCss();

        try {
            Mvp.loadFxml(this, fxmlUrl,
                    x -> x.setResources(Language.getInstance().getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(this);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        stage.setScene(scene);

        stage.setOnShowing(event -> stage.sizeToScene());
        stage.setOnShown(event -> presenter.onStartView());
        Mvp.hookWindowCloseRequest(stage, event -> presenter.onStopView());

        scene.setOnKeyReleased(x -> {
            if (x.getCode() == KeyCode.ESCAPE) {
                presenter.onStopView();
            }
        });
    }

    @FXML
    public void initialize() {
        closeButton.setOnAction(event -> presenter.onStopView());

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                RecordBox box = new RecordBox();
                recordBoxList.add(box);
                recordGridPane.add(box, j + 1, i + 1);
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

    @Override
    public Window getWindow() {
        return stage;
    }

    @Override
    public void startView(int analysisDataId) {
        presenter.showAnalysisData(analysisDataId);

        stage.show();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setHeaderText(header)
                .setContentText(throwable.toString()).setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void setSongText(String text) {
        songTextField.setText(text);
    }

    @Override
    public void setTitleImage(Image image) {
        titleImageView.setImage(image);
    }

    @Override
    public void setTitleText(String text) {
        titleTextField.setText(text);
    }

    @Override
    public void setRecordBoxData(int row, int column, RecordBoxData data) {
        RecordBox recordBox = recordBoxList.get(row * 4 + column);

        recordBox.maxComboCheckBox.setSelected(data.maxCombo);
        recordBox.maxComboImageView.setImage(data.maxComboImage);
        recordBox.rateImageView.setImage(data.rateImage);
        recordBox.rateTextField.setText(data.rateText);
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

            super.add(wrapWithBox(rateImageView), 0, 0);
            super.add(wrapWithBox(maxComboImageView), 1, 0);
            super.add(rateTextField, 0, 1);
            super.add(maxComboCheckBox, 1, 1);

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
