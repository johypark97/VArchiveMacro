package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.CaptureViewerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.CaptureViewerStage;
import java.lang.ref.WeakReference;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class CaptureViewerViewImpl
        extends AbstractMvpView<CaptureViewerPresenter, CaptureViewerView>
        implements CaptureViewerView {
    private WeakReference<CaptureViewerComponent> captureViewerComponentReference;

    private CaptureViewerComponent getCaptureViewerComponent() {
        return captureViewerComponentReference.get();
    }

    @Override
    public void showImage(Image image) {
        getCaptureViewerComponent().setImage(image);
    }

    @Override
    protected Stage newStage() {
        CaptureViewerStage stage = new CaptureViewerStage();

        captureViewerComponentReference = new WeakReference<>(stage.captureViewerComponent);

        stage.initOwner(getPresenter().getStartData().ownerWindow);

        stage.setOnShown(event -> {
            Stage source = (Stage) event.getSource();

            getCaptureViewerComponent().setCloseButtonAction(() -> getPresenter().stopPresenter());

            source.getScene().setOnKeyReleased(x -> {
                if (x.getCode() == KeyCode.ESCAPE) {
                    getPresenter().stopPresenter();
                }
            });
        });

        return stage;
    }
}
