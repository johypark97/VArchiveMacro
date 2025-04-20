package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.CaptureViewer.CaptureViewerPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.CaptureViewer.CaptureViewerView;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

public class CaptureViewerViewImpl extends BorderPane implements CaptureViewerView {
    private static final String FXML_PATH = "/fxml/CaptureViewer.fxml";

    private final ImageViewer imageViewer = new ImageViewer();

    private final Stage stage;

    @MvpPresenter
    public CaptureViewerPresenter presenter;

    @FXML
    public Button closeButton;

    public CaptureViewerViewImpl(Stage stage) {
        this.stage = stage;

        URL fxmlUrl = CaptureViewerViewImpl.class.getResource(FXML_PATH);
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
        setCenter(imageViewer);

        imageViewer.setStyle("-fx-background-color: black; -fx-cursor: MOVE;");

        closeButton.setOnAction(event -> presenter.onStopView());
    }

    @Override
    public Window getWindow() {
        return stage;
    }

    @Override
    public void startView(Image image) {
        presenter.showImage(image);

        stage.show();
    }

    @Override
    public void setImage(Image image) {
        imageViewer.setImage(image);
    }
}
