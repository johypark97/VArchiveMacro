package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.HomeComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerSafeGlassComponent;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HomeStage extends Stage {
    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    public final HomeComponent homeComponent = new HomeComponent();
    public final ScannerComponent scannerComponent;
    public final ScannerDjNameInputComponent scannerDjNameInputComponent;
    public final ScannerSafeGlassComponent scannerSafeGlassComponent =
            new ScannerSafeGlassComponent();

    public HomeStage(HomeView view) {
        scannerComponent = new ScannerComponent(view);
        scannerDjNameInputComponent = new ScannerDjNameInputComponent(view);

        homeComponent.scannerTab.setContent(
                new StackPane(scannerComponent, scannerDjNameInputComponent,
                        scannerSafeGlassComponent));

        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

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
