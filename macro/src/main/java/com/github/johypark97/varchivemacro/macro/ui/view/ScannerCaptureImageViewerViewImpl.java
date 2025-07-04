package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerCaptureImageViewer;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.CaptureImageViewerViewModel;
import java.io.IOException;
import java.net.URL;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class ScannerCaptureImageViewerViewImpl extends BorderPane
        implements ScannerCaptureImageViewer.ScannerCaptureImageViewerView {
    private static final String FXML_PATH = "/fxml/ScannerCaptureImageViewer.fxml";

    private final ImageViewer imageViewer = new ImageViewer();

    @FXML
    private TextField filterTextField;

    @FXML
    private Button filterResetButton;

    @FXML
    private ListView<CaptureImageViewerViewModel.CaptureImage> captureImageListView;

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

        setCenter(imageViewer);
        imageViewer.getStyleClass().add("image-viewer");

        closeButton.setOnAction(event -> presenter.requestStopStage());

        Platform.runLater(closeButton::requestFocus);
    }

    @Override
    public void setCaptureImageList(
            ObservableList<CaptureImageViewerViewModel.CaptureImage> value) {
        captureImageListView.setItems(value);
    }

    @Override
    public void showCaptureImage(Image value) {
        imageViewer.setImage(value);
    }
}
