package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorView;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.lang.ref.WeakReference;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class LinkEditorComponent extends BorderPane {
    private static final String FXML_FILE_NAME = "LinkEditor.fxml";

    private final ImageViewer imageViewer = new ImageViewer();
    private final WeakReference<LinkEditorView> viewReference;

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

    public LinkEditorComponent(LinkEditorView view) {
        viewReference = new WeakReference<>(view);

        URL url = LinkEditorComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
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
                getView().updateSearch(value);
            }
        });

        resetButton.setOnAction(event -> searchTextField.clear());

        findAllCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String pattern = searchTextField.getText().trim();
            getView().showCaptureDataList(pattern, newValue);
        });

        captureDataListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    linkButton.setDisable(newValue == null);

                    if (newValue != null) {
                        getView().showCaptureImage(newValue.idProperty().get());
                    }
                });

        unlinkButton.setOnAction(event -> getView().unlinkCaptureData());

        linkButton.setDisable(true);
        linkButton.setOnAction(event -> {
            CaptureData selected = captureDataListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                getView().linkCaptureData(selected.idProperty().get());
            }
        });

        closeButton.setOnAction(event -> getView().requestStop());
    }

    public void setSplitPaneDividerPositions(double value) {
        // the divider position will reset when invoked at initialization, so it must be invoked
        // after the window is shown.
        centerSplitPane.setDividerPositions(value);
    }

    public void setSongText(String text) {
        songTextField.setText(text);
    }

    public void setCaptureDataList(ObservableList<CaptureData> list) {
        captureDataListView.setItems(list);
    }

    public void showImage(Image image) {
        imageViewer.setImage(image);
    }

    private LinkEditorView getView() {
        return viewReference.get();
    }
}
