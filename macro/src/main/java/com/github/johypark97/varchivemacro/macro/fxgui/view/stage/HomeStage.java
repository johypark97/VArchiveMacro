package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.HomeComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.MacroComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerSafeGlassComponent;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HomeStage extends Stage {
    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    public final HomeComponent homeComponent;
    public final MacroComponent macroComponent = new MacroComponent();
    public final ScannerComponent scannerComponent;
    public final ScannerDjNameInputComponent scannerDjNameInputComponent;
    public final ScannerSafeGlassComponent scannerSafeGlassComponent =
            new ScannerSafeGlassComponent();

    public HomeStage(HomeView view) {
        homeComponent = new HomeComponent(view);
        scannerComponent = new ScannerComponent(view);
        scannerDjNameInputComponent = new ScannerDjNameInputComponent(view);

        homeComponent.scannerTab.setContent(
                new StackPane(scannerDjNameInputComponent, scannerSafeGlassComponent));

        homeComponent.macroTab.setContent(macroComponent);

        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(homeComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(String.format("%s v%s", TITLE, BuildInfo.version));

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_HEIGHT);
        setMinWidth(STAGE_WIDTH);
    }

    public void replaceScannerTabContent() {
        homeComponent.scannerTab.setContent(scannerComponent);
    }
}
