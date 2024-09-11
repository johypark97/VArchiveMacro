package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CaptureViewerStage {
    private static final String TITLE = "Capture Viewer";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private static final int STAGE_MIN_HEIGHT = 200;
    private static final int STAGE_MIN_WIDTH = 200;

    public static Stage create() {
        Stage stage = new Stage();
        setupStage(stage);
        return stage;
    }

    public static void setupStage(Stage stage) {
        URL iconUrl = GlobalResource.getIcon();

        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle(TITLE);

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_MIN_HEIGHT);
        stage.setMinWidth(STAGE_MIN_WIDTH);
    }
}
