package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class CaptureViewerComponent extends BorderPane {
    private static final String FXML_FILE_NAME = "CaptureViewer.fxml";

    private final ImageViewer imageViewer = new ImageViewer();

    @FXML
    public Label helpLmbLabel;

    @FXML
    public Label helpMmbLabel;

    @FXML
    public Label helpRmbLabel;

    @FXML
    public Button closeButton;

    public CaptureViewerComponent() {
        URL url = CaptureViewerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        helpLmbLabel.setText("Left Mouse Button: Move");
        helpMmbLabel.setText("Middle Mouse Button: Zoom in/out");
        helpRmbLabel.setText("Right Mouse Button: Reset");

        setCenter(imageViewer);

        imageViewer.setStyle("-fx-background-color: black; -fx-cursor: MOVE;");
    }

    public void setCloseButtonAction(Runnable runnable) {
        closeButton.setOnAction(event -> runnable.run());
    }

    public void setImage(Image image) {
        imageViewer.setImage(image);
    }
}
