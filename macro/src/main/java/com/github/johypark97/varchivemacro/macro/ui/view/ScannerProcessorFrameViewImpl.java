package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFrame;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ScannerProcessorFrameViewImpl extends BorderPane
        implements ScannerProcessorFrame.ScannerProcessorFrameView {
    private static final String FXML_PATH = "/fxml/ScannerProcessorFrame.fxml";

    @FXML
    private Label conflictLabel;

    @FXML
    private Label selectLabel;

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
    public ScannerProcessorFrame.ScannerProcessorFramePresenter presenter;

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
    }
}
