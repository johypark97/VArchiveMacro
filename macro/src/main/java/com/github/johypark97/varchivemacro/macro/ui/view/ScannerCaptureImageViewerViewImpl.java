package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerCaptureImageViewer;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ScannerCaptureImageViewerViewImpl extends BorderPane
        implements ScannerCaptureImageViewer.ScannerCaptureImageViewerView {
    private static final String FXML_PATH = "/fxml/ScannerCaptureImageViewer.fxml";

    @FXML
    private TextField filterTextField;

    @FXML
    private Button filterResetButton;

    @FXML
    private ListView<String> captureImageListView;

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
    }
}
