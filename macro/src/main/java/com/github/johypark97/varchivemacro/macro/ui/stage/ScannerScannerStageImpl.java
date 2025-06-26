package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScanner;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScannerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerScannerViewImpl;
import java.awt.Toolkit;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
        ContextManager.INSTANCE.clearScannerContext();

        presenter =
                new ScannerScannerPresenterImpl(this, ContextManager.INSTANCE.getGlobalContext(),
                        ContextManager.INSTANCE.getScannerContext());

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
    public void showError(String content, Throwable throwable) {
        showError(null, content, throwable);
    }

    @Override
    public void showError(String header, String content, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setContentText(content)
                .setThrowable(throwable).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showWarning(String content) {
        Alert alert = AlertBuilder.warning().setOwner(stage).setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showInformation(String content) {
        showInformation(null, content);
    }

    @Override
    public void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(stage).setContentText(content).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
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
