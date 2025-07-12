package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFrame;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ScannerProcessorFrameViewImpl extends BorderPane
        implements ScannerProcessorFrame.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorFrame.fxml";

    @FXML
    private Label reviewLabel;

    @FXML
    private Label analysisLabel;

    @FXML
    private Label uploadLabel;

    @FXML
    private Button captureImageViewerButton;

    @FXML
    private Button leftButton;

    @FXML
    private Button rightButton;

    @MvpPresenter
    public ScannerProcessorFrame.Presenter presenter;

    public ScannerProcessorFrameViewImpl() {
        URL fxmlUrl = ScannerProcessorFrameViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        captureImageViewerButton.setOnAction(event -> presenter.showCaptureImageViewer());
    }

    @Override
    public void setCenterNode(Node value) {
        setCenter(value);
    }

    @Override
    public void setLeftButtonFunction(ScannerProcessorFrame.ButtonFunction value) {
        leftButton.setOnAction(value.eventHandler());
        leftButton.setText(value.text());
    }

    @Override
    public void setRightButtonFunction(ScannerProcessorFrame.ButtonFunction value) {
        rightButton.setOnAction(value.eventHandler());
        rightButton.setText(value.text());
    }
}
