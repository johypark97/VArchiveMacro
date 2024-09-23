package com.github.johypark97.varchivemacro.macro.fxgui.ui.linkeditor;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.GlobalResource;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LinkEditorStage {
    private static final String TITLE = "Link Editor";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

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

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }
}
