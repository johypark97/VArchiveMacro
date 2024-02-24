package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.stage;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.LiveTesterComponent;
import java.net.URL;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LiveTesterStage extends Stage {
    private static final String TITLE = "OCR Live Tester";

    private static final String GLOBAL_CSS_FILENAME = "global.css";

    public final LiveTesterComponent liveTesterComponent = new LiveTesterComponent();

    public LiveTesterStage() {
        URL globalCss = LiveTesterStage.class.getResource(GLOBAL_CSS_FILENAME);
        Objects.requireNonNull(globalCss);

        Scene scene = new Scene(liveTesterComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        setScene(scene);

        setAlwaysOnTop(true);
        setTitle(TITLE);
    }
}
