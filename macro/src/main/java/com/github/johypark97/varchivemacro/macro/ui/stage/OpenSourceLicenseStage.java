package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class OpenSourceLicenseStage {
    private static final String TITLE = "Open Source License";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    public static Stage create() {
        Stage stage = new Stage();
        setupStage(stage);
        return stage;
    }

    public static void setupStage(Stage stage) {
        URL iconUrl = UiResource.getIcon();

        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle(TITLE);

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);
    }
}
