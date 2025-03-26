package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.resource.GlobalResource;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.LinkEditor.LinkEditorPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.LinkEditor.LinkEditorView;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class LinkEditorViewImpl extends BorderPane implements LinkEditorView {
    private static final String FXML_PATH = "/fxml/LinkEditor.fxml";

    private final ImageViewer imageViewer = new ImageViewer();

    private final Stage stage;

    @MvpPresenter
    public LinkEditorPresenter presenter;

    @FXML
    public TextField songTextField;

    @FXML
    public SplitPane centerSplitPane;

    @FXML
    public TextField searchTextField;

    @FXML
    public Button resetButton;

    @FXML
    public CheckBox findAllCheckBox;

    @FXML
    public ListView<CaptureData> captureDataListView;

    @FXML
    public Button unlinkButton;

    @FXML
    public Button linkButton;

    @FXML
    public Button closeButton;

    public LinkEditorViewImpl(Stage stage) {
        this.stage = stage;

        URL fxmlUrl = LinkEditorViewImpl.class.getResource(FXML_PATH);
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

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

        stage.setOnShown(event -> {
            // The divider position will reset when invoked at initialization, so it must be invoked
            // after the window is shown.
            centerSplitPane.setDividerPositions(0.3);
            presenter.onStartView();
        });
        Mvp.hookWindowCloseRequest(stage, event -> presenter.onStopView());
    }

    @FXML
    public void initialize() {
        centerSplitPane.getItems().add(imageViewer);

        imageViewer.setStyle("-fx-background-color: black; -fx-cursor: MOVE;");

        captureDataListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(CaptureData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(String.format("(%d) %s", item.idProperty().get(), item.scannedTitle.get()));
            }
        });

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if (!value.equals(oldValue.trim())) {
                presenter.updateCaptureDataListFilter(value);
            }
        });

        resetButton.setOnAction(event -> searchTextField.clear());

        findAllCheckBox.selectedProperty().addListener(
                (observable, oldValue, newValue) -> presenter.showCaptureDataList(
                        searchTextField.getText().trim(), newValue));

        captureDataListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    linkButton.setDisable(newValue == null);

                    if (newValue != null) {
                        presenter.showCaptureImage(newValue.idProperty().get());
                    }
                });

        unlinkButton.setOnAction(event -> presenter.unlinkCaptureData());

        linkButton.setDisable(true);
        linkButton.setOnAction(event -> {
            CaptureData selected = captureDataListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                presenter.linkCaptureData(selected.idProperty().get());
            }
        });

        closeButton.setOnAction(event -> presenter.onStopView());
    }

    @Override
    public Window getWindow() {
        return stage;
    }

    @Override
    public void startView() {
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
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void setSongText(String text) {
        songTextField.setText(text);
    }

    @Override
    public void setCaptureDataList(ObservableList<CaptureData> list) {
        captureDataListView.setItems(list);
    }

    @Override
    public void setCaptureImage(Image image) {
        imageViewer.setImage(image);
    }
}
