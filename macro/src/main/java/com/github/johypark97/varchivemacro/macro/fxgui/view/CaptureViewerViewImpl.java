package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.CaptureViewerStage;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class CaptureViewerViewImpl extends BorderPane implements CaptureViewerView {
    private static final String FXML_FILE_NAME = "CaptureViewer.fxml";

    private final ImageViewer imageViewer = new ImageViewer();

    private final CaptureViewerStage stage;

    @MvpPresenter
    public CaptureViewerPresenter presenter;

    @FXML
    public Button closeButton;

    public CaptureViewerViewImpl(CaptureViewerStage stage) {
        this.stage = stage;

        try {
            URL url = CaptureViewerViewImpl.class.getResource(FXML_FILE_NAME);
            Mvp.loadFxml(this, url,
                    x -> x.setResources(Language.getInstance().getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        setCenter(imageViewer);

        imageViewer.setStyle("-fx-background-color: black; -fx-cursor: MOVE;");

        closeButton.setOnAction(event -> stage.stopStage());
    }

    @Override
    public void setImage(Image image) {
        imageViewer.setImage(image);
    }
}
