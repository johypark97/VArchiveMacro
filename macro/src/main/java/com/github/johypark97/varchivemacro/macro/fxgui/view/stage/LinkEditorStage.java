package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.LinkEditorViewImpl;
import java.net.URL;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Window;

public class LinkEditorStage extends AbstractCommonStage {
    private static final String TITLE = "Link Editor";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final LinkEditorViewImpl view = new LinkEditorViewImpl(this);

    public LinkEditorStage(Window owner, ScannerModel scannerModel, Runnable onStopStage) {
        super(onStopStage);

        setupView(scannerModel);
        setupStage(owner);
    }

    public void showStage(Path cacheDirectoryPath, int songDataId, Runnable onUpdateLink) {
        view.startView(cacheDirectoryPath, songDataId, onUpdateLink);
        show();
    }

    private void setupView(ScannerModel scannerModel) {
        LinkEditorPresenterImpl presenter = new LinkEditorPresenterImpl();
        presenter.linkModel(scannerModel);
        Mvp.linkViewAndPresenter(view, presenter);
    }

    private void setupStage(Window owner) {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(view);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(TITLE);

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_HEIGHT / 2.0);
        setMinWidth(STAGE_WIDTH / 2.0);

        setOnShown(event -> view.setSplitPaneDividerPositions(0.3));
    }
}
