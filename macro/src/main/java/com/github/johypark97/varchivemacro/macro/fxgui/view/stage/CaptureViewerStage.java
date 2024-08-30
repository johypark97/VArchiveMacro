package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.CaptureViewerViewImpl;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class CaptureViewerStage extends AbstractCommonStage {
    private static final String TITLE = "Capture Viewer";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private static final int STAGE_MIN_HEIGHT = 200;
    private static final int STAGE_MIN_WIDTH = 200;

    private final CaptureViewerViewImpl view = new CaptureViewerViewImpl(this);

    public CaptureViewerStage(Window owner, Runnable onStopStage) {
        super(onStopStage);

        setupView();
        setupStage(owner);
    }

    public void showStage(Image image) {
        view.setImage(image);
        show();
    }

    private void setupView() {
        Mvp.linkViewAndPresenter(view, new CaptureViewerPresenterImpl());
    }

    private void setupStage(Window owner) {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(view);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        initOwner(owner);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(TITLE);

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_MIN_HEIGHT);
        setMinWidth(STAGE_MIN_WIDTH);

        scene.setOnKeyReleased(x -> {
            if (x.getCode() == KeyCode.ESCAPE) {
                hide();
            }
        });
    }
}
