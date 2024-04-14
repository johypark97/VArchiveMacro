package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.macro.fxgui.view.component.CaptureViewerComponent;
import java.net.URL;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CaptureViewerStage extends Stage {
    private static final String TITLE = "Capture Viewer";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private static final int STAGE_MIN_HEIGHT = 200;
    private static final int STAGE_MIN_WIDTH = 200;

    public final CaptureViewerComponent captureViewerComponent = new CaptureViewerComponent();

    public CaptureViewerStage() {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(captureViewerComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        setTitle(TITLE);

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_MIN_HEIGHT);
        setMinWidth(STAGE_MIN_WIDTH);
    }
}
