package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.GlobalResource;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HomeStage {
    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    public static Stage create() {
        Stage stage = new Stage();
        setupStage(stage);
        return stage;
    }

    public static void setupStage(Stage stage) {
        URL iconUrl = GlobalResource.getIcon();

        stage.getIcons().add(new Image(iconUrl.toString()));
        stage.setTitle(String.format("%s v%s", TITLE, BuildInfo.version));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);
    }
}
