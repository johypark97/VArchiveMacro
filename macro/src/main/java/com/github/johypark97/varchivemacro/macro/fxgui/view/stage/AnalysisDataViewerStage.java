package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.AnalysisDataViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.AnalysisDataViewerViewImpl;
import java.net.URL;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Window;

public class AnalysisDataViewerStage extends AbstractCommonStage {
    private static final String TITLE = "Analysis Data Viewer";

    private static final int STAGE_MIN_HEIGHT = 270;
    private static final int STAGE_MIN_WIDTH = 480;

    private final AnalysisDataViewerViewImpl view = new AnalysisDataViewerViewImpl(this);

    public AnalysisDataViewerStage(Window owner, ScannerModel scannerModel, Runnable onStopStage) {
        super(onStopStage);

        setupView(scannerModel);
        setupStage(owner);
    }

    public void showStage(Path cacheDirectoryPath, int analysisDataId) {
        view.showAnalysisData(cacheDirectoryPath, analysisDataId);
        show();
    }

    private void setupView(ScannerModel scannerModel) {
        AnalysisDataViewerPresenterImpl presenter = new AnalysisDataViewerPresenterImpl();
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

        initOwner(owner);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(TITLE);

        setMinHeight(STAGE_MIN_HEIGHT);
        setMinWidth(STAGE_MIN_WIDTH);

        setOnShowing(event -> sizeToScene());

        scene.setOnKeyReleased(x -> {
            if (x.getCode() == KeyCode.ESCAPE) {
                hide();
            }
        });
    }
}
