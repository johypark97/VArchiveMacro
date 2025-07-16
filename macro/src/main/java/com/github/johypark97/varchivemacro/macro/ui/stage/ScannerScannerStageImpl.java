package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.ui.manager.StageManager;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScanner;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScannerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerScannerViewImpl;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ScannerScannerStageImpl extends AbstractBaseStage implements ScannerScannerStage {
    private static final int STAGE_HEIGHT = 600;
    private static final int STAGE_WIDTH = 800;

    private final StageManager stageManager;

    private final Runnable onStop;

    private ScannerScanner.Presenter presenter;

    public ScannerScannerStageImpl(AbstractTreeableStage parent, StageManager stageManager,
            Runnable onStop) {
        super(parent);

        this.stageManager = stageManager;

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.setTitle(Language.INSTANCE.getString("scanner.scanner.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        presenter =
                new ScannerScannerPresenterImpl(this, ContextManager.INSTANCE.getGlobalContext());

        ScannerScannerViewImpl view = new ScannerScannerViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();
    }

    @Override
    public void showScannerTester() {
        stageManager.showScannerTester(this);
    }

    @Override
    protected boolean onStopStage() {
        if (!presenter.stopView()) {
            return false;
        }

        onStop.run();

        return true;
    }
}
