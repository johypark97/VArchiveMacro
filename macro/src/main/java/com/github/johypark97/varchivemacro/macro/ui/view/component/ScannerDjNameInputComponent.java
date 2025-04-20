package com.github.johypark97.varchivemacro.macro.ui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ScannerDjNameInputComponent extends VBox {
    private static final String FXML_PATH = "/fxml/ScannerDjNameInput.fxml";

    public Runnable onLoad;

    @FXML
    public Label djNameErrorLabel;

    @FXML
    public Tooltip djNameErrorTooltip;

    @FXML
    public TextField djNameTextField;

    @FXML
    public Button loadButton;

    public ScannerDjNameInputComponent() {
        URL url = ScannerDjNameInputComponent.class.getResource(FXML_PATH);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
    }

    @FXML
    public void initialize() {
        setVisible(false);

        djNameErrorLabel.setVisible(false);
        djNameErrorTooltip.setShowDelay(Duration.ZERO);

        loadButton.setOnAction(event -> onLoad.run());
    }

    public String getDjNameText() {
        return djNameTextField.getText();
    }

    public void showError(String message) {
        djNameErrorTooltip.setText(message);
        djNameErrorLabel.setVisible(true);
    }

    public void hideError() {
        djNameErrorLabel.setVisible(false);
    }

    public void upEffect() {
        setEffect(new GaussianBlur(5));
        setOpacity(0.5);
    }

    public void downEffect() {
        setEffect(null);
        setOpacity(1);
    }
}
