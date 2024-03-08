package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class HomeComponent extends BorderPane {
    private static final String FXML_FILENAME = "Home.fxml";

    @FXML
    public Tab scannerTab;

    @FXML
    public Tab macroTab;

    public HomeComponent() {
        URL url = HomeComponent.class.getResource(FXML_FILENAME);
        MvpFxml.loadRoot(this, url);
    }
}
