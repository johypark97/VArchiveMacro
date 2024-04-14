package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.CaptureViewerView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.CaptureViewer.StartData;
import java.util.Objects;

public class CaptureViewerPresenterImpl
        extends AbstractMvpPresenter<CaptureViewerPresenter, CaptureViewerView>
        implements CaptureViewerPresenter {
    public StartData startData;

    @Override
    public StartData getStartData() {
        return startData;
    }

    @Override
    public void setStartData(StartData value) {
        startData = value;
    }

    @Override
    public void updateView() {
        getView().showImage(startData.image);
    }

    @Override
    protected CaptureViewerPresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        Objects.requireNonNull(startData);
        Objects.requireNonNull(startData.image);
        Objects.requireNonNull(startData.ownerWindow);

        return true;
    }
}
