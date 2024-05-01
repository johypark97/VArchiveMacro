package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.macro.fxgui.view.component.AnalysisDataViewerComponent;
import java.net.URL;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AnalysisDataViewerStage extends Stage {
    private static final String TITLE = "Analysis Data Viewer";

    private static final int STAGE_MIN_HEIGHT = 270;
    private static final int STAGE_MIN_WIDTH = 480;

    public final AnalysisDataViewerComponent analysisDataViewerComponent =
            new AnalysisDataViewerComponent();

    public AnalysisDataViewerStage() {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(analysisDataViewerComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        setTitle(TITLE);

        setMinHeight(STAGE_MIN_HEIGHT);
        setMinWidth(STAGE_MIN_WIDTH);
    }
}
