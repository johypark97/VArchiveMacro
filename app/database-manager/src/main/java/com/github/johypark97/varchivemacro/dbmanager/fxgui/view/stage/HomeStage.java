package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.stage;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.HomeComponent;
import java.net.URL;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeStage extends Stage {
    private static final String TITLE = "Database Manager";

    private static final String GLOBAL_CSS_FILENAME = "global.css";
    private static final String TABLE_COLOR_CSS_FILENAME = "table-color.css";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    public final HomeComponent homeComponent;

    public HomeStage(HomeView view) {
        homeComponent = new HomeComponent(view);

        URL globalCss = HomeStage.class.getResource(GLOBAL_CSS_FILENAME);
        Objects.requireNonNull(globalCss);

        URL tableColorCss = HomeStage.class.getResource(TABLE_COLOR_CSS_FILENAME);
        Objects.requireNonNull(tableColorCss);

        Scene scene = new Scene(homeComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        setTitle(TITLE);

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_HEIGHT);
        setMinWidth(STAGE_WIDTH);
    }
}
