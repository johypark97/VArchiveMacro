package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.OpenSourceLicenseComponent;
import java.net.URL;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenSourceLicenseStage extends Stage {
    private static final String TITLE = "Open Source License";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    public final OpenSourceLicenseComponent openSourceLicenseComponent;

    public OpenSourceLicenseStage(OpenSourceLicenseView view) {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        openSourceLicenseComponent = new OpenSourceLicenseComponent(view);

        Scene scene = new Scene(openSourceLicenseComponent);
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
