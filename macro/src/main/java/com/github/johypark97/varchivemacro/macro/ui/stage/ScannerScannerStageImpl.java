package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScanner;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScannerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerScannerViewImpl;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ScannerScannerStageImpl extends AbstractTreeableStage implements ScannerScannerStage {
    private static final int STAGE_HEIGHT = 600;
    private static final int STAGE_WIDTH = 800;

    private final Runnable onStop;

    private ScannerScanner.ScannerScannerPresenter presenter;

    public ScannerScannerStageImpl(AbstractTreeableStage parent, Runnable onStop) {
        super(parent);

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
        presenter = new ScannerScannerPresenterImpl();

        ScannerScannerViewImpl view = new ScannerScannerViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();
    }

    @Override
    public void focusStage() {
        stage.requestFocus();
    }

    @Override
    protected boolean onStopStage() {
        onStop.run();

        return true;
    }
}
