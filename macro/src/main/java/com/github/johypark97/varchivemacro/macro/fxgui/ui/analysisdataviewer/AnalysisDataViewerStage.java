package com.github.johypark97.varchivemacro.macro.fxgui.ui.analysisdataviewer;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.GlobalResource;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AnalysisDataViewerStage {
    private static final String TITLE = "Analysis Data Viewer";

    private static final int STAGE_MIN_HEIGHT = 270;
    private static final int STAGE_MIN_WIDTH = 480;

    public static Stage create() {
        Stage stage = new Stage();
        setupStage(stage);
        return stage;
    }

    public static void setupStage(Stage stage) {
        URL iconUrl = GlobalResource.getIcon();

        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle(TITLE);

        stage.setMinHeight(STAGE_MIN_HEIGHT);
        stage.setMinWidth(STAGE_MIN_WIDTH);
    }
}
