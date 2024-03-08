package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class ScannerComponent extends TabPane {
    private static final String FXML_FILE_NAME = "Scanner.fxml";

    public ScannerComponent() {
        URL url = ScannerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setVisible(false);
    }
}
