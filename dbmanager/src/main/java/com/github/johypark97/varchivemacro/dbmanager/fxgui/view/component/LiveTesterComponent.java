package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class LiveTesterComponent extends VBox {
    private static final String FXML_FILENAME = "LiveTester.fxml";

    @FXML
    public ImageView imageView;

    @FXML
    public TextField ocrTextField;

    @FXML
    public TextField recognizedSongTextField;

    public LiveTesterComponent() {
        URL url = LiveTesterComponent.class.getResource(FXML_FILENAME);
        MvpFxml.loadRoot(this, url);
    }
}
