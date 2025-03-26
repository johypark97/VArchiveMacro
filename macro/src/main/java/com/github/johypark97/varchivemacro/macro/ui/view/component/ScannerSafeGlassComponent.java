package com.github.johypark97.varchivemacro.macro.ui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ScannerSafeGlassComponent extends VBox {
    private static final String FXML_PATH = "/fxml/ScannerSafeGlass.fxml";

    @FXML
    public ProgressIndicator progressIndicator;

    @FXML
    public StackPane forbiddenMark;

    @FXML
    public Label messageLabel;

    public ScannerSafeGlassComponent() {
        URL url = ScannerSafeGlassComponent.class.getResource(FXML_PATH);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        forbiddenMark.setVisible(false);

        messageLabel.setText(null);
    }

    public void showLoadingMark() {
        forbiddenMark.setVisible(false);
        progressIndicator.setVisible(true);
    }

    public void showForbiddenMark() {
        forbiddenMark.setVisible(true);
        progressIndicator.setVisible(false);
    }

    public void setText(String value) {
        messageLabel.setText(value);
    }
}
