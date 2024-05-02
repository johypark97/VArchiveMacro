package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.LinkEditorComponent;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LinkEditorStage extends Stage {
    private static final String TITLE = "Link Editor";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    public final LinkEditorComponent linkEditorComponent;

    public LinkEditorStage(LinkEditorView view) {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        linkEditorComponent = new LinkEditorComponent(view);

        Scene scene = new Scene(linkEditorComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(TITLE);

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_HEIGHT / 2.0);
        setMinWidth(STAGE_WIDTH / 2.0);
    }
}
